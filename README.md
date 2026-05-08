# ifg-simple-eda
#### -Muhammad Fadly-

Aplikasi ini adalah sistem pemrosesan pesanan berbasis Cloud-Native menggunakan **Quarkus Framework** dan **Apache Kafka**. Sistem ini mendemonstrasikan implementasi integrasi Kafka, transformasi data, mekanisme retry, dan penanganan kesalahan menggunakan *Dead Letter Queue* (DLQ).

## Fitur Utama
- **RESTful API**: Endpoint untuk menerima data pesanan.
- **Message Broker**: Integrasi dengan Apache Kafka menggunakan SmallRye Reactive Messaging.
- **Data Transformation**: Klasifikasi otomatis status pesanan (`HIGH` vs `NORMAL`).
- **Resilience**: Mekanisme retry hingga 3 kali untuk menangani kegagalan sementara.
- **Error Handling**: Pengalihan pesan gagal ke *Dead Letter Queue* (DLQ) untuk audit data.

---

## 1. Prasyarat (Prerequisites)
Sebelum menjalankan aplikasi, pastikan perangkat Anda sudah terinstal:
- **Java 17** atau versi terbaru.
- **Maven**.
- **Docker & Docker Compose**.

---

## 2. Cara Menjalankan Aplikasi

### 1: Jalankan Infrastruktur (Kafka)
Aplikasi membutuhkan Kafka Broker. Anda dapat menjalankan Kafka secara cepat menggunakan Docker Compose.

```bash
docker network create kafka-network
docker-compose -f docker-compose-kafka.yaml up -d
```
###  2: Jalankan Aplikasi via docker
Aplikasi bisa pada docker dengan menjalankan perintah berikut
```bash
./mvnw package
docker-compose -f docker-compose-app.yaml up --build
```

###  3: Jalankan Aplikasi via IDE
Aplikasi juga bisa dijalankan dengan membuka code menggunakan IDE yang biasa anda gunakan seperti Itellij Idea, vs code dan lain-lain, lalu jalankan perintah berikut :
```bash
./mvnw quarkus:dev
```

Aplikasi akan berjalan dengan port 8080

---

## 3. Cara Pengujian Endpoint
Gunakan Postman, Apidog atau tools pengujian API lainnya, dengan http method POST, dan reuqest body JSON seperti berikut 

### 1. Positive case request
```bash
{
    "id": "TEST-ORDER-001",
    "amount": 500.0
}
```

### 2. Negative case request (dengan 3x retry)
```bash
{
    "id": "failed",
    "amount": 500.0
}
```
### 3. Response 
```bash
{
    "responseCode": "000",
    "responseMessage": "SUCCESS : Order has been processed successfully",
    "orders": {
        "id": "TEST-ORDER-001",
        "amount": 900.0,
        "status": null
    }
}
```

## 3. Penjelasan Alur Proses
Sistem mengikuti alur kerja reactive sebagai berikut:

### 1. Producer (Entry Point):
User mengirimkan payload JSON melalui REST API. Controller menerima data tersebut sebagai objek RequestOrderDto dan mempublikasikannya ke Kafka topic bernama ORDERS melalui channel orders-out.

### 2. Consumer (Processing Unit):
Service Consumer mengambil pesan dari topic orders melalui channel orders-in. Di tahap ini, logika yang diterapkan:
- Jika amount > 1000, maka kategori diset HIGH.
- Selain itu, kategori diset NORMAL.

### 3. Processed (Output):
Setelah berhasil ditransformasi menjadi objek ResponseOrderDto, hasilnya dikirimkan ke topic orders-processed.

### 4. Error Handling & DLQ:
Jika terjadi kegagalan (misal: database down atau error sistem), anotasi @Retry akan melakukan percobaan ulang hingga 3 kali. Jika setelah 3 kali tetap gagal, pesan dikirim ke topic orders-dlq (Dead Letter Queue) menggunakan strategi failure-strategy=dead-letter-queue. Hal ini memastikan tidak ada data yang hilang (no data loss) dan memudahkan tim untuk melakukan investigasi manual.