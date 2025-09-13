/usr/bin/mc config host add myminio http://minio:9000 ${MINIO_ROOT_USER} ${MINIO_ROOT_PASSWORD}

/usr/bin/mc mb myminio/avatars --ignore-existing

/usr/bin/mc policy set download myminio/avatars

echo "MinIO bucket 'avatars' is ready."
exit 0
