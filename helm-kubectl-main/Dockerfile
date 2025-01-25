FROM alpine:3.12

# Установка curl
RUN apk add --no-cache curl

# Установка Helm
RUN curl -sSL https://get.helm.sh/helm-v3.11.3-linux-amd64.tar.gz | tar -xz -C /tmp \
    && mv /tmp/linux-amd64/helm /usr/local/bin/helm

# Установка kubectl
RUN curl -sSL https://dl.k8s.io/release/v1.26.4/bin/linux/amd64/kubectl -o /usr/local/bin/kubectl

# Установка прав доступа
RUN chmod +x /usr/local/bin/helm /usr/local/bin/kubectl

# Проверка установки
RUN helm version && kubectl version --client
