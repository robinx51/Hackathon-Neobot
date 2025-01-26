import os
from dotenv import load_dotenv
import psycopg2
from psycopg2.extras import execute_batch
from faker import Faker
import random
from datetime import datetime, timedelta
import time

load_dotenv()

DB_CONFIG = {
    "host": os.getenv('DB_HOST'),
    "port": os.getenv('DB_PORT'),
    "database": os.getenv('DB_NAME'),
    "user": os.getenv('DB_USER'),
    "password": os.getenv('DB_PASSWORD'),
    "options": f"-c role=read-write",
    "sslmode": "require"
}

fake = Faker('ru_RU')
generated_telegram_ids = set()

translit_dict = {
    'а': 'a', 'б': 'b', 'в': 'v', 'г': 'g', 'д': 'd', 'е': 'e',
    'ё': 'e', 'ж': 'zh', 'з': 'z', 'и': 'i', 'й': 'y', 'к': 'k',
    'л': 'l', 'м': 'm', 'н': 'n', 'о': 'o', 'п': 'p', 'р': 'r',
    'с': 's', 'т': 't', 'у': 'u', 'ф': 'f', 'х': 'h', 'ц': 'ts',
    'ч': 'ch', 'ш': 'sh', 'щ': 'sch', 'ъ': '', 'ы': 'y', 'ь': '',
    'э': 'e', 'ю': 'yu', 'я': 'ya'
}

def transliterate(text):
    return ''.join([translit_dict.get(c, c) for c in text.lower()])

def generate_telegram_id():
    while True:
        telegram_id = random.randint(100000000, 9999999999)
        if telegram_id not in generated_telegram_ids:
            generated_telegram_ids.add(telegram_id)
            return telegram_id

def generate_user():
    user = {
        'telegram_id': generate_telegram_id(),
        'role': 'candidate' if random.random() < 0.2 else 'visitor',
        'first_name': fake.first_name(),
        'last_name': fake.last_name(),
        'email': transliterate(fake.first_name() + fake.last_name()) + "@example.com",
        'city': fake.city_name(),
        'phone_number': '79' + ''.join(random.choices('0123456789', k=9))
    }
    return user

def generate_statement(user_ids):
    return {
        'user_id': random.choice(user_ids),
        'course_id': random.randint(1, 7),
        'statement_status': random.choices(
            ['PRE_APPLICATION', 'ACCEPTED', 'REJECTED', 'PENDING'],
            weights=[0.1, 0.2, 0.2, 0.5]
        )[0],
        'creation_date': fake.date_time_between('-1y', 'now'),
        'changed_date': None
    }

def log_event(event, message):
    try:
        log_conn = psycopg2.connect(**DB_CONFIG)
        log_conn.autocommit = True
        log_cursor = log_conn.cursor()

        log_cursor.execute(
            """INSERT INTO logs.data_logs (event, time_of_event, message) 
               VALUES (%s, %s, %s)""",
            (event, datetime.now(), message)
        )
    except Exception as e:
        print(f"Ошибка при логировании: {str(e)}")
    finally:
        if log_conn:
            log_cursor.close()
            log_conn.close()

def main():
    users = [generate_user() for _ in range(100)]
    user_ids = list(range(1, 101))
    
    statements = []
    for _ in range(300):
        stmt = generate_statement(user_ids)
        if stmt['statement_status'] != 'PENDING':
            stmt['changed_date'] = stmt['creation_date'] + timedelta(days=random.randint(1, 30))
        else:
            stmt['changed_date'] = stmt['creation_date']
        statements.append(stmt)

    log_event('INFO', 'Начинаем загрузку данных в БД...')
    print("Начинаем загрузку данных в БД...")

    try:
        conn = psycopg2.connect(**DB_CONFIG)
        cursor = conn.cursor()

        cursor.execute("SELECT nextval('users_user_id_seq');")
        current_user_id = cursor.fetchone()[0]

        cursor.execute("SELECT nextval('statements_statement_id_seq');")
        current_statement_id = cursor.fetchone()[0]

        user_records = [
            (
                current_user_id + i + 1,
                u['telegram_id'],
                u['role'],
                u['first_name'],
                u['last_name'],
                u['email'],
                u['city'],
                u['phone_number']
            )
            for i, u in enumerate(users)
        ]
        
        execute_batch(cursor,
            """INSERT INTO users 
            (user_id, telegram_id, role, first_name, last_name, email, city, phone_number)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s)""",
            user_records
        )

        statement_records = [
            (
                current_statement_id + i + 1,
                stmt['user_id'],
                stmt['course_id'],
                stmt['statement_status'],
                stmt['creation_date'],
                stmt['changed_date']
            )
            for i, stmt in enumerate(statements)
        ]
        
        execute_batch(cursor,
            """INSERT INTO statements 
            (statement_id, user_id, course_id, statement_status, creation_date, changed_date)
            VALUES (%s, %s, %s, %s, %s, %s)""",
            statement_records
        )

        cursor.execute("SELECT setval('users_user_id_seq', (SELECT MAX(user_id) FROM dev.users));")
        cursor.execute("SELECT setval('statements_statement_id_seq', (SELECT MAX(statement_id) FROM dev.statements));")

        conn.commit()

        log_event('SUCCESS', 'Данные успешно загружены в базу данных')
        print("Данные успешно загружены в базу данных")

    except Exception as e:
        log_event('FAILED', f'Ошибка: {str(e)}')
        print(f"Ошибка: {str(e)}")
        if conn:
            conn.rollback()
    finally:
        if conn:
            cursor.close()
            conn.close()

if __name__ == "__main__":
    main()