echo "Aguardando o MinIO iniciar..."

until mc alias set myminio http://minio:9000 minioadmin minioadmin; do
  echo "zzz... MinIO ainda não está pronto. Tentando novamente em 2s..."
  sleep 2
done

echo "MinIO conectado!"

echo "Criando bucket 'capas-albuns'..."
mc mb --ignore-existing myminio/capas-albuns

echo "Configurando política de acesso (Download Público)..."
mc anonymous set download myminio/capas-albuns

echo "Fazendo upload das imagens de Seed..."
mc cp /images/* myminio/capas-albuns/

echo "Setup do MinIO concluído! Imagens disponíveis."