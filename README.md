## Доменные имена

Для приложений вы можете выбрать любой поддомен четвертого уровня на домене *.nh2025.codenrock.com.
Помните, что вы не одни, поэтому придумайте себе уникальный поддомен, который не может совпасть с другими. Если вдруг
совпадет, то ингресс не создастся

## SSL

В неймспейсе, выделенном на команду, уже находится wildcard сертификат. Но если что, есть cluster issuer letsencrypt,
который сможет создать сертификат для домена 4-го уровня

## Доступ к сервисам

### Kubernetes

На вашу команду выделен kubernetes namespace. Для подключения к нему используйте утилиту `kubectl` и `*.kube.config`
файл, который вам выдадут организаторы.

Состояние namespace, работающие pods и логи приложений можно посмотреть по
адресу [https://dashboard.nh2025.codenrock.com/](https://dashboard.nh2025.codenrock.com/). Для открытия дашборда
необходимо выбрать авторизацию через Kubeconfig и указать путь до выданного вам `*.kube.config` файла

### База данных

На каждую команду созданы базы данных Postgres.

Для подключения к Postgres используйте следующую команду:

```
psql "host=rc1b-gk21kywyncnoym7q.mdb.yandexcloud.net \
      port=6432 \
      sslmode=verify-full \
      dbname=$DB_NAME \
      user=$DB_USERNAME \
      target_session_attrs=read-write"
```

`rc1b-gk21kywyncnoym7q.mdb.yandexcloud.net` - адрес хоста в кластере Yandex.Cloud. Подробнее
в [документации](https://cloud.yandex.ru/docs/managed-postgresql/). Не забудьте скачать и
установить [SSL сертификат](https://cloud.yandex.ru/docs/managed-postgresql/operations/connect#get-ssl-cert).

### Доступы к базе данных и Kubernetes кластеру

В гитлабе, в вашей группе, где находится репозиторий проекта, находится репозиторий credentials. В нем находятся учетные
данные для доступа к базе данных и к кластеру Kubernetes


**Чтобы запустить backend-часть в докере, необходимо создать внутреннюю сеть для них, собрать с параметром -t и запустить во внутренней сети.** 

Пример:

```
docker network create hackathon

docker build -t data-storage_image .
docker build -t api-service_image .

docker run -d --name data --network hackathon data-storage_image
docker run -d --name api --network hackathon -p 8081:8081 api-service_image
```
