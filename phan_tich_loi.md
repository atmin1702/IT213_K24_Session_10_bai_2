# Phân Tích Lỗi Code Tích Hợp SDK Tracing

Đoạn mã cấu hình và tích hợp Langfuse SDK cũ gặp phải 3 vấn đề nghiêm trọng đối với một hệ thống tài chính (RikkeiPay):

## 1. Lỗi Hardcode API Keys (Bảo mật)
- **Vấn đề:** Việc gắn cứng (hardcode) trực tiếp `pk-lf-...` và `sk-lf-...` trong file `LangfuseConfig.java` là vi phạm nguyên tắc bảo mật tối kỵ.
- **Rủi ro:** Khi mã nguồn được đẩy lên Git (GitHub/GitLab), bất kỳ ai có quyền truy cập repository đều có thể lấy được API Key và gửi log rác vào hệ thống Langfuse, thao túng dữ liệu hoặc làm cạn kiệt tài nguyên (DDoS/Quota exhaustion).

## 2. Rò rỉ thông tin nhạy cảm PII (Data Privacy)
- **Vấn đề:** Trong `TransferService.java`, lập trình viên truyền trực tiếp dữ liệu dạng plain-text chứa tên người dùng (`user`), tài khoản nhận (`toAccount`) và số tiền (`amount`) vào `input` và `output` của trace.
- **Rủi ro:** Langfuse lưu trữ toàn bộ các chuỗi này. Bất kỳ kỹ sư DevOps hay nhân viên nào có quyền truy cập vào Langfuse Dashboard đều có thể đọc được lịch sử giao dịch chi tiết của khách hàng. Điều này vi phạm nghiêm trọng các tiêu chuẩn bảo mật tài chính (như PCI-DSS hoặc GDPR).

## 3. Thiếu định danh tập trung (Session / User ID)
- **Vấn đề:** Trace được tạo ra chỉ có tên là `bank-transfer` nhưng không có `.userId()` và `.sessionId()`.
- **Rủi ro:** Khi xảy ra lỗi hoặc khách hàng khiếu nại, bộ phận CSKH/Kỹ thuật không thể gom nhóm (grouping) hay tìm kiếm (filter) các hành động liên tiếp của cùng một khách hàng trong một phiên giao dịch. Việc truy vết (Tracing) trở nên vô dụng trên quy mô hàng ngàn giao dịch/ngày.
