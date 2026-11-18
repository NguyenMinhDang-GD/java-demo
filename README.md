# VetCare – Veterinary Clinic Management System (Demo & Prototype)

> **Môn học:** Phân tích & Đặc tả Yêu cầu Phần mềm  
> **Dự án:** Hệ thống Quản lý Phòng khám Thú y VetCare  

---

## 1. Cấu trúc thư mục dự án (Project Structure)

Dự án được phân bổ khoa học và độc lập thành 2 phần tương ứng với 2 tiêu chí chấm điểm của đồ án:

```text
demo/
├── UI/                     👉 [10%] Giao diện Web Prototype tương tác (HTML/CSS/JS)
│   ├── index.html          • Dashboard SaaS y tế, tích hợp đổi 3 vai trò
│   ├── app.js              • Logic điều hướng, phân quyền, modal đặt lịch, tiếp đón và thanh toán
│   └── styles.css          • Tokens thiết kế, badge trạng thái và hiệu ứng
│
├── java-demo/              👉 [10%] Ứng dụng Desktop Java Swing chạy thực thi trực tiếp
│   ├── src/                • Toàn bộ mã nguồn Java (Model, Service, UI, TestRunner)
│   ├── data/               • Dữ liệu lưu trữ mẫu (Persistence store)
│   ├── compile-and-test.bat • Kịch bản tự động biên dịch & chạy kiểm thử tự động
│   ├── run-java-demo.bat   • Kịch bản 1-click khởi chạy giao diện Desktop Java Swing
│   └── README.md           • Tài liệu hướng dẫn
│
└── README.md               • Hướng dẫn tổng thể toàn bộ dự án
```

---

## 2. Hướng dẫn khởi chạy ứng dụng (Quick Start Guide)

### 2.1. Hướng dẫn chạy PHẦN 1: Giao diện Web Prototype (10% UI/Prototype)
* **Yêu cầu môi trường:** Không cần cài đặt phần mềm phụ trợ, chỉ cần trình duyệt web (Google Chrome, Microsoft Edge, Firefox, Cốc Cốc, Safari).
* **Cách chạy:**
  * **Cách 1 (Nhanh nhất):** Mở thư mục `demo/UI/`, nhấp đúp chuột vào file `index.html`.
  * **Cách 2 (VS Code Live Server):** Mở thư mục `demo/UI/` trong VS Code, nhấp chuột phải vào `index.html` $\rightarrow$ Chọn **Open with Live Server**.
* **Tính năng:**
  * Thanh đổi vai trò (Role Switcher) trên thanh Topbar: chuyển đổi linh hoạt giữa **Receptionist**, **Veterinarian**, và **Admin**.
  * Hiển thị banner quyền hạn, tự động cập nhật menu điều hướng và cảnh báo quy tắc nghiệp vụ (`BR-03`, `BR-05`, `FR-03`, `FR-10`).
  * Thực hiện đầy đủ luồng: Đặt lịch hẹn chống trùng lịch (`BR-02`), Tiếp đón đo sinh hiệu (`FR-13`), Khám EMR & kê đơn thuốc (`FR-06`), Tiêm chủng có sàng lọc lâm sàng (`BR-04`), Thu ngân xuất hóa đơn tự động (`FR-09`).

---

### 2.2. Hướng dẫn chạy PHẦN 2: Ứng dụng Desktop Java Swing (10% Java Demo)
* **Yêu cầu môi trường:**
  * Đã cài đặt **Java JDK 11 trở lên** (khuyến nghị JDK 17 hoặc JDK 21).
  * Lệnh `java` và `javac` đã có trong biến môi trường `PATH`. Kiểm tra bằng:
    ```powershell
    java -version
    javac -version
    ```
* **Cách chạy:**
  * **Cách 1 (Khởi chạy bằng 1 Click):**
    Vào thư mục `demo/java-demo/`, nhấp đúp chuột vào file **`run-java-demo.bat`**. Cửa sổ ứng dụng **VetCare Clinic Management System** kích thước 1180x780 sẽ xuất hiện.
  * **Cách 2 (Khởi chạy bằng Terminal / PowerShell):**
    ```powershell
    cd "f:\HK1- 26-27\PhanTichYeuCau\demo\java-demo"
    .\compile-and-test.bat
    java -cp out vetcare.ui.VetCareSwingApp
    ```
  * **Cách 3 (Chạy kiểm thử tự động TestRunner):**
    Nhấp đúp vào file **`compile-and-test.bat`** để chạy qua toàn bộ các kiểm thử đơn vị tự động.

---

## 3. Chức năng theo từng tài khoản (Role Matrix)

Hệ thống hỗ trợ cả 2 bộ tài khoản đăng nhập demo (mật khẩu mặc định hoặc mật khẩu demo):

| Tài khoản (Username) | Mật khẩu đề xuất | Mật khẩu dự phòng | Vai trò (Role) | Người đại diện |
|---|---|---|---|---|
| `admin` | `admin123` | `123456` | **Admin** | Clinic Manager (Quản trị viên) |
| `receptionist` (hoặc `reception`) | `reception123` | `123456` | **Receptionist** | Sarah Jenkins / Nguyễn Thanh Tiền (Lễ tân) |
| `vet` (hoặc `dr.khoi`) | `vet123` | `123456` | **Veterinarian** | Dr. Minh Khôi / Dr. Nguyễn Minh (Bác sĩ thú y) |

### Ma trận phân quyền Menu / Chức năng trên giao diện:

| Menu / Chức năng | Admin | Receptionist | Veterinarian | Ghi chú nghiệp vụ |
|---|:---:|:---:|:---:|---|
| **Dashboard** | ✓ | ✓ | ✓ | Tổng quan chỉ số, hàng đợi tiếp nhận hôm nay |
| **Owners & Pets** | ✓ | ✓ | — | Quản lý danh bạ chủ nuôi & thú cưng (`UC-02`, `BR-01`, `FR-12`) |
| **Appointments** | ✓ | ✓ | — | Đặt lịch khám, chống trùng slot bác sĩ (`UC-03`, `BR-02`) |
| **Check-in & Queue** | ✓ | ✓ | — | Tiếp đón, ghi nhận cân nặng & thân nhiệt (`FR-13`) |
| **Invoices** | ✓ | ✓ | — | Thu ngân, xuất viện phí & thanh toán (`UC-06`, `BR-05`, `FR-09`) |
| **Examination / EMR** | — | — | ✓ | Khám lâm sàng, chẩn đoán, cảnh báo dị ứng (`UC-04`, `BR-03`, `FR-06`) |
| **Prescriptions** | — | — | ✓ | Kê đơn thuốc điều trị theo phác đồ an toàn (`FR-06`) |
| **Vaccinations** | — | — | ✓ | Tiêm chủng, sàng lọc lâm sàng & tính ngày nhắc (`UC-05`, `BR-04`, `FR-07`) |
| **Services** | ✓ | — | — | Cấu hình bảng giá danh mục dịch vụ, thuốc, vaccine (`FR-03`) |
| **Staff Accounts** | Placeholder | — | — | Quản lý & khóa/mở tài khoản nhân viên (`FR-10`) |

> **Lưu ý:** Menu *Staff Accounts* hiện là giao diện quản trị placeholder / công tắc khóa tài khoản (`FR-10`). Các chỉ số minh họa trên Dashboard phản ánh dữ liệu hoạt động trong ngày của phiên làm việc.

---

## 4. Kịch bản kiểm thử toàn hệ thống (TC01 – TC13)

> **Điều kiện tiên quyết:** Chạy ứng dụng thành công, có thể đăng nhập cả ba tài khoản. Nên sử dụng dữ liệu thử mới để dễ phân biệt với dữ liệu demo có sẵn. Nếu số điện thoại ví dụ đã tồn tại, hãy dùng số thử khác chưa có trong hệ thống.

### TC01 – Authentication (Admin)
* **Thao tác:**
  1. Đăng nhập với tài khoản `admin / admin123` (hoặc `123456`).
  2. Kiểm tra giao diện Dashboard xuất hiện với đầy đủ quyền hạn Admin.
  3. Đăng xuất; thử đăng nhập lại với mật khẩu sai.
* **Kết quả mong đợi:** Đăng nhập đúng thành công; đăng nhập sai bị từ chối và hiển thị thông báo lỗi.

### TC02 – Owners (Admin hoặc Receptionist)
* **Thao tác:**
  1. Đăng nhập tài khoản Receptionist (`receptionist / reception123`).
  2. Mở mục **Owners & Pets**, thêm chủ nuôi mới: Tên `Test Owner`, điện thoại `0987654321`, email `test@example.com`.
  3. Thử thêm chủ nuôi thứ hai với cùng số điện thoại `0987654321`.
* **Kết quả mong đợi:** Chủ nuôi được tạo thành công lần đầu; lần thứ hai số điện thoại bị trùng sẽ bị hệ thống chặn lại (`FR-12`).

### TC03 – Pets (Receptionist)
* **Thao tác:**
  1. Trong mục **Owners & Pets**, thêm thú cưng thuộc `Test Owner`.
  2. Nhập thông tin: Tên `Lucky`, loài `Dog`, giống `Poodle`, tuổi `2`.
* **Kết quả mong đợi:** Thú cưng `Lucky` xuất hiện trong danh sách và liên kết chính xác với chủ nuôi `Test Owner` (`BR-01`).

### TC04 – Appointments (Receptionist)
* **Thao tác:**
  1. Mở mục **Appointments**, tạo lịch hẹn cho bé `Lucky`.
  2. Chọn bác sĩ `Dr. Nguyen Minh` (hoặc `Dr. Minh Khoi`) và một ngày/giờ trong tương lai.
  3. Ghi lại mã lịch hẹn để sử dụng cho các test tiếp theo.
* **Kết quả mong đợi:** Lịch hẹn được tạo thành công với trạng thái `Scheduled`. Nếu chọn trùng khung giờ của bác sĩ, hệ thống sẽ chặn (`BR-02`).

### TC05 – Check-in (Receptionist)
* **Thao tác:**
  1. Mở mục **Check-in & Queue** (hoặc chọn ca hẹn trong bảng lịch hẹn).
  2. Chọn lịch hẹn của `Lucky`, nhập cân nặng `5.5 kg`, thân nhiệt `38.5 °C`, ghi chú `Healthy`.
  3. Xác nhận check-in.
* **Kết quả mong đợi:** Trạng thái chuyển thành `Checked-in`; thông tin sinh hiệu được ghi nhận (`FR-13`) và bệnh nhân xuất hiện trong hàng đợi của Bác sĩ.

### TC06 – Examination / EMR (Veterinarian)
* **Thao tác:**
  1. Nhấn Log out (hoặc Switch Role), đăng nhập tài khoản Bác sĩ `vet / vet123`.
  2. Mở mục **Examination / EMR**, chọn lịch hẹn của `Lucky` đã check-in.
  3. Nhấn **Start Examination** (hoặc chọn bệnh nhân từ hàng đợi), nhập:
     * *Symptoms:* `Cough`
     * *Diagnosis:* `Mild infection`
     * *Treatment:* `Medication and rest`
  4. Lưu hồ sơ khám.
* **Kết quả mong đợi:** Medical Record (EMR) được tạo; ca khám được ký nhận và chuyển trạng thái sang `Completed` (`BR-03`, `FR-06`).

### TC07 – Prescriptions (Veterinarian)
* **Thao tác:**
  1. Mở mục **Prescriptions** (hoặc phần kê đơn trong EMR), nhấn **+ Create Prescription**.
  2. Chọn hồ sơ khám của `Lucky` vừa hoàn thành.
  3. Thêm thuốc với dữ liệu ví dụ:
     * *Medicine:* `Amoxicillin`
     * *Dosage:* `1 tablet`
     * *Frequency:* `Twice daily`
     * *Days:* `5`
  4. Lưu và dùng **View Prescription** để kiểm tra.
* **Kết quả mong đợi:** Đơn thuốc được tạo và hiển thị đúng các trường đã nhập. Dữ liệu thuốc chỉ phục vụ kiểm thử phần mềm, không phải hướng dẫn điều trị thú y.

### TC08 – Vaccinations (Veterinarian)
* **Thao tác:**
  1. Mở mục **Vaccinations**, nhấn **+ Record Vaccination** (hoặc phần chỉ định vaccine trong form khám).
  2. Chọn `Lucky`, vaccine `Rabies`, ngày tiêm là ngày hiện tại, chu kỳ nhắc lại 1 năm (365 ngày).
  3. Đảm bảo đã tích chọn *"Passed clinical screening"* (`BR-04`).
  4. Lưu và kiểm tra chi tiết.
* **Kết quả mong đợi:** Lịch sử tiêm chủng và ngày tiêm nhắc lại tự động được ghi nhận chính xác (`FR-07`).

### TC09 – Services (Admin)
* **Thao tác:**
  1. Đăng xuất, đăng nhập tài khoản Admin (`admin / admin123`).
  2. Mở mục **Services** (hoặc Catalog), kiểm tra dịch vụ `General Examination`.
  3. Nhấn **+ Add Service**, nhập tên `Test Service`, giá `100000 VND` (hoặc `$15.00`) và lưu.
* **Kết quả mong đợi:** Dịch vụ mới xuất hiện trong bảng giá; các dịch vụ có sẵn vẫn được giữ nguyên (`FR-03`).

### TC10 – Invoices (Admin hoặc Receptionist)
* **Thao tác:**
  1. Trong tài khoản Admin hoặc Receptionist, mở mục **Invoices**.
  2. Nhấn **+ Create Invoice** (hoặc Generate Invoice), chọn ca khám của `Lucky` đã `Completed`.
  3. Thêm dịch vụ khám với số lượng 1.
  4. Xác nhận tạo hóa đơn.
* **Kết quả mong đợi:** Hóa đơn được tạo với trạng thái `Unpaid`; tổng tiền bằng tổng chi phí dịch vụ khám + tiền thuốc + tiền vaccine (`FR-09`). Nếu ca khám chưa `Completed`, hệ thống chặn xuất hóa đơn (`BR-05`).

### TC11 – Payment (Admin hoặc Receptionist)
* **Thao tác:**
  1. Trong mục **Invoices**, chọn hóa đơn vừa tạo của `Lucky`.
  2. Nhấn **Record Payment** (hoặc Pay Invoice), chọn hình thức `Cash` (Tiền mặt) và xác nhận.
  3. Thử thanh toán lại hóa đơn đó lần thứ hai.
* **Kết quả mong đợi:** Trạng thái chuyển sang `Paid`, lưu phương thức thanh toán; nút thanh toán bị vô hiệu hóa, không cho thanh toán lại lần hai.

### TC12 – Data Persistence (Kiểm tra bằng cả ba tài khoản)
* **Thao tác:**
  1. Đóng hoàn toàn ứng dụng.
  2. Khởi động lại ứng dụng:
     * Chạy lại file `run-java-demo.bat` (hoặc lệnh `mvn clean compile exec:java`).
  3. Đăng nhập Receptionist hoặc Admin: Kiểm tra `Test Owner`, thú cưng `Lucky`, lịch hẹn, dịch vụ mới, hóa đơn và trạng thái `Paid`.
  4. Đăng xuất, đăng nhập Veterinarian: Kiểm tra Medical Record, Prescription và Vaccination của `Lucky`.
* **Kết quả mong đợi:** Tất cả dữ liệu vừa tạo vẫn còn nguyên vẹn; trạng thái `Completed` và `Paid` được giữ nguyên sau khi khởi động lại ứng dụng.

### TC13 – Role Permissions (Kiểm tra cả ba tài khoản)
* **Thao tác:**
  1. **Đăng nhập Admin:** Kiểm tra thấy đầy đủ các phân hệ quản lý: Owners & Pets, Appointments, Check-in & Queue, Invoices, Services, Staff Accounts.
  2. **Đăng nhập Receptionist:** Kiểm tra thấy Owners & Pets, Appointments, Check-in & Queue, Invoices; không truy cập được chức năng cấu hình dịch vụ hoặc các menu khám bệnh/kê đơn của bác sĩ (`BR-03`).
  3. **Đăng nhập Veterinarian:** Kiểm tra thấy Examination / EMR, Prescriptions, Vaccinations; không can thiệp vào thu tiền Invoices hoặc cấu hình Services (`BR-05`).
* **Kết quả mong đợi:** Menu và các màn hình hiển thị đúng theo bảng phân quyền ở Mục 3. Đây là kiểm thử phân quyền giao diện và luồng nghiệp vụ.

---

## 5. Bảng tổng hợp kết quả kiểm thử (Test Summary)

Kết quả ghi nhận sau khi thực hiện kiểm thử toàn bộ hệ thống:

| Test Case | Chức năng kiểm thử | Kết quả | Ghi chú đối chiếu |
|:---:|---|:---:|---|
| **TC01** | Authentication (Đăng nhập & Xác thực) | **PASS** | `UC-01`, `FR-01` |
| **TC02** | Owners (Quản lý chủ nuôi & Chống trùng SĐT) | **PASS** | `UC-02`, `FR-12` |
| **TC03** | Pets (Quản lý thú cưng & Liên kết chủ) | **PASS** | `UC-02`, `BR-01` |
| **TC04** | Appointments (Đặt lịch & Chống trùng slot) | **PASS** | `UC-03`, `BR-02` |
| **TC05** | Check-in & Queue (Tiếp đón & Ghi nhận sinh hiệu) | **PASS** | `UC-03`, `FR-13` |
| **TC06** | Examination / EMR (Khám bệnh lâm sàng) | **PASS** | `UC-04`, `BR-03`, `FR-06` |
| **TC07** | Prescriptions (Kê đơn thuốc điều trị an toàn) | **PASS** | `UC-04`, `FR-06` |
| **TC08** | Vaccinations (Tiêm phòng, sàng lọc & tính ngày nhắc) | **PASS** | `UC-05`, `BR-04`, `FR-07` |
| **TC09** | Services (Cấu hình bảng giá danh mục) | **PASS** | `UC-07`, `FR-03` |
| **TC10** | Invoices (Tự động tổng hợp chi phí hóa đơn) | **PASS** | `UC-06`, `BR-05`, `FR-09` |
| **TC11** | Payment (Ghi nhận thanh toán & Chặn trùng) | **PASS** | `UC-06` |
| **TC12** | Data Persistence (Lưu trữ dữ liệu bền vững) | **PASS** | Serialization / Storage |
| **TC13** | Role Permissions (Phân quyền 3 vai trò giao diện) | **PASS** | `UC-01`, `BR-03`, `BR-05` |

**Tổng kết:** **13/13 PASS (100%)** trong đợt kiểm thử toàn diện hệ thống. Đáp ứng trọn vẹn và vượt mức các yêu cầu kiểm thử của đồ án môn học.
