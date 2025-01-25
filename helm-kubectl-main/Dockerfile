FROM alpine:latest

# Задайте нужные переменные окружения
ARG KUBE_VERSION=1.24.0
ARG HELM_VERSION=3.10.0
ARG YQ_VERSION=4.15.1
ARG TARGETOS=linux
ARG TARGETARCH=amd64

# Устанавливаем зависимости
RUN apk -U upgrade \
    && apk add --no-cache ca-certificates bash git openssh curl gettext jq docker \
    && wget -q https://dl.k8s.io/release/v${KUBE_VERSION}/bin/linux/amd64/kubectl -O /usr/local/bin/kubectl \
    && wget -q https://get.helm.sh/helm-v${HELM_VERSION}-linux-amd64.tar.gz -O /tmp/helm.tar.gz \
    && tar -xz -C /tmp -f /tmp/helm.tar.gz linux-amd64/helm && mv /tmp/linux-amd64/helm /usr/local/bin/helm \
    && wget -q https://github.com/mikefarah/yq/releases/download/v${YQ_VERSION}/yq_linux_amd64 -O /usr/local/bin/yq \
    && chmod +x /usr/local/bin/helm /usr/local/bin/kubectl /usr/local/bin/yq \
    && mkdir /config \
    && chmod g+rwx /config /root \
    && kubectl version --client \
    && helm version \
    && uname -a \
    && docker --version  # Проверка установки Docker

# Остальная конфигурация Dockerfile
