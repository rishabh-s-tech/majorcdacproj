# Just the official Prometheus image with our config baked in - no build steps needed
# beyond that, since Prometheus itself is a pre-built binary.
FROM prom/prometheus:v3.0.1
COPY prometheus.yml /etc/prometheus/prometheus.yml
