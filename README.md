### Justifikasi Profiling
Profiling pada InventoryApplication dilakukan menggunakan IntelliJ Profiler dengan metode Flame Graph yang menampilkan CPU Time sebagai metrik utama. Saya memilih IntelliJ 
Profiler karena toolini terintegrasi langsung dengan IDE tanpa memerlukan konfigurasi agen eksternal, sehingga meminimalkan overhead setup dan memungkinkan profiling dilakukan 
pada lingkungan development yang identik dengan kondisi aktual aplikasi. Saya menjalankan dua sesi profiling secara terpisah — sesi pertama pada pukul 6:01 PM dan sesi kedua pada 
pukul 7:49 PM — dengan tujuan membandingkan perilaku CPU sebelum dan sesudah perubahan yang saya lakukan pada lapisan service, sehingga dampak perubahan dapat diukur secara objektif 
dan tidak hanya bersifat asumsi.

Dari hasil flame graph yang saya peroleh, ditemukan bahwa hampir seluruh area grafik berwarna gelap, yang menunjukkan bahwa CPU aktif hanya pada sebagian kecil waktu eksekusi. 
Hotspot yang paling signifikan teridentifikasi pada metode getAllProducts di sisi kiri bawah flame graph, dengan java.lang.Thread.run mencatatkan +2.356 library calls, 
angka yang jauh melampaui ekspektasi normal untuk sebuah operasi fetch produk. Di sisi kanan, SpringApplication.run dan InventoryApplication.main muncul sebagai entry point 
standar Spring Boot yang tidak bermasalah. Temuan utama saya adalah bahwa getAllProducts berpotensi melakukan pemanggilan berulang ke layer bawah, baik berupa N+1 query ke database
maupun serialisasi JSON berulang — yang terakumulasi menjadi ribuan library calls dalam satu siklus eksekusi.

Berdasarkan hasil profiling ini, saya merekomendasikan tiga langkah perbaikan yang perlu segera ditindaklanjuti. Pertama, getAllProducts harus 
direfaktor untuk menggunakan pagination melalui Pageable di Spring Data JPA, sehingga tidak seluruh data produk dimuat sekaligus ke memori. 
Kedua, perlu dilakukan investigasi lebih dalam menggunakan tab Call Tree dan Method List yang tersedia di IntelliJ Profiler untuk menelusuri asal dari 2.356 
library calls tersebut hingga ke level method spesifik. Ketiga, saya berencana menjalankan sesi profiling tambahan dengan metrik Wall Clock Time dan Memory Allocation 
karena CPU Time saja tidak cukup untuk menggambarkan bottleneck pada aplikasi yang bersifat I/O-heavy seperti inventory system ini, 
ada kemungkinan latensi sesungguhnya tersembunyi di sisi koneksi database yang tidak terdeteksi pada sesi profiling ini.
