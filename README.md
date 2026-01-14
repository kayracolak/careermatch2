# CareerMatch: Yapay Zeka Destekli LinkedIn Kariyer Asistanı

**CareerMatch**, üniversite öğrencilerinin akademik transkriptlerini analiz ederek, yetkinliklerine en uygun iş ilanlarını bulan ve kişiselleştirilmiş kariyer tavsiyeleri sunan, Kotlin ve Jetpack Compose ile geliştirilmiş modern bir Android uygulamasıdır.

Bu proje, geleneksel iş arama süreçlerini yapay zeka (OpenAI GPT-4o) ile birleştirerek, adayın akademik geçmişi ile sektör gereksinimleri arasındaki boşluğu doldurmayı hedefler.

## 🚀 Proje Hakkında

Öğrenciler genellikle mezun olduklarında hangi iş ilanlarına uygun olduklarını veya eksik yetkinliklerini belirlemekte zorlanırlar. CareerMatch bu sorunu şu şekilde çözer:

1.  **Transkript Analizi:** Kullanıcı PDF formatındaki transkriptini yükler. Uygulama, OCR ve PDF işleme teknolojileri ile dersleri ve notları ayrıştırır.
2.  **AI Eşleşmesi:** Ayrıştırılan akademik profil, OpenAI API kullanılarak gerçek zamanlı iş ilanlarının gereksinimleri ile karşılaştırılır.
3.  **Skorlama ve Tavsiye:** Her iş ilanı için 0-100 arasında bir "Uyum Skoru" üretilir ve adaya eksik yönlerini nasıl geliştirebileceğine dair spesifik tavsiyeler verilir.

## 🛠️ Teknoloji Yığını ve Mimari

Proje, modern Android geliştirme standartlarına uygun olarak **MVVM (Model-View-ViewModel)** mimarisi üzerine inşa edilmiştir.

### Temel Teknolojiler
* **Dil:** Kotlin (%100)
* **UI Toolkit:** Jetpack Compose (Material3 Design System)
* **Asenkron İşlemler:** Coroutines & Flow
* **Ağ (Networking):** Retrofit & OkHttp
* **Dependency Injection:** Manual Dependency Injection (ViewModelFactory pattern)

### Backend & Servisler
* **Firebase Auth:** Güvenli kullanıcı kimlik doğrulama ve oturum yönetimi.
* **Firebase Firestore:** Kullanıcı verileri, transkript metinleri ve favori ilanların bulutta saklanması.
* **Firebase Storage:** PDF dosyalarının güvenli depolanması.

### Yapay Zeka ve Veri İşleme
* **OpenAI API (GPT-4o-mini):** Kariyer analizi, yetkinlik eşleştirme ve mülakat simülasyonu mantığı.
* **PDFBox-Android:** Cihaz üzerinde PDF metin ayrıştırma (Text Extraction).
* **RapidAPI (LinkedIn Jobs):** Gerçek zamanlı iş ilanı verilerinin çekilmesi.

## ✨ Temel Özellikler

* **PDF Transkript Okuma:** Cihaz depolamasından PDF seçimi ve metin çıkarma.
* **Akıllı İş Arama:** Lokasyon ve unvan bazlı güncel iş ilanı arama motoru.
* **Detaylı Uyumluluk Raporu:** İş ilanı ile öğrenci profili arasında detaylı analiz (Güçlü yönler, Eksik yetkinlikler, Tavsiyeler).
* **Favoriler Sistemi:** İlgilenilen ilanları kaydetme ve daha sonra tekrar analiz etme imkanı.
* **Ekstra Bilgi Yönetimi:** Transkriptte yer almayan staj ve proje deneyimlerinin analize dahil edilmesi.
