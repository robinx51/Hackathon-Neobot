FROM alpine:3.20

ARG KUBE_VERSION
ARG HELM_VERSION
ARG TARGETOS=linux
ARG TARGETARCH=amd64
ARG YQ_VERSION

# Обновление пакетов и установка зависимостей
RUN apk -U upgrade && \
    apk add --no-cache ca-certificates bash git openssh curl gettext jq docker && \
    curl -LO https://dl.k8s.io/release/${KUBE_VERSION}/bin/${TARGETOS}/${TARGETARCH}/kubectl && \
    mv kubectl /usr/local/bin/ && \
    curl -sSL https://get.helm.sh/helm-${HELM_VERSION}-${TARGETOS}-${TARGETARCH}.tar.gz | tar -xz -C /tmp && \
    mv /tmp/linux-amd64/helm /usr/local/bin/helm && \
    curl -LO https://github.com/mikefarah/yq/releases/download/v${YQ_VERSION}/yq_${TARGETOS}_${TARGETARCH} && \
    mv yq_${TARGETOS}_${TARGETARCH} /usr/local/bin/yq && \
    chmod +x /usr/local/bin/helm /usr/local/bin/kubectl /usr/local/bin/yq && \
    mkdir /config && \
    chmod g+rwx /config /root && \
    kubectl version --client && \
    helm version && \
    uname -a && \
    docker --version
Здесь использован curl вместо wget, и для Helm добавлена команда для распаковки в /tmp с перемещением в /usr/local/bin/.

Попробуйте это решение и сообщите, если ошибка сохраняется!
WORKDIR /config

# Команда по умолчанию
CMD [ "bash" ]
