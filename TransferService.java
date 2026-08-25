package com.rikkeipay.service;

import io.langfuse.client.LangfuseClient;
import io.langfuse.client.model.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransferService {

    @Autowired
    private LangfuseClient langfuseClient;

    public void processTransfer(String userId, String sessionId, String toAccount, double amount) {
        // PII Masking: Ẩn thông tin tài khoản ngân hàng nhạy cảm
        String maskedAccount = maskAccount(toAccount);
        
        // Tạo Trace với đầy đủ SessionId và UserId để dễ dàng truy vết tập trung
        Trace trace = langfuseClient.trace(new Trace()
            .name("bank-transfer")
            .userId(userId)
            .sessionId(sessionId)
            .input("Yêu cầu chuyển tiền đến tài khoản: " + maskedAccount + ", số tiền: " + amount));

        try {
            System.out.println("Processing transfer...");
            // Logic xử lý chuyển khoản thực tế tại đây...
            
            // Output cũng không chứa PII dạng plain-text
            trace.output("Giao dịch thành công. Đã chuyển tiền đến tài khoản: " + maskedAccount);
        } catch (Exception e) {
            trace.output("Giao dịch thất bại: " + e.getMessage());
            throw e;
        }
    }

    // Hàm tiện ích để che thông tin tài khoản (chỉ giữ lại 4 số cuối)
    private String maskAccount(String account) {
        if (account == null || account.length() < 4) return "***";
        return account.substring(0, account.length() - 4).replaceAll(".", "*") + account.substring(account.length() - 4);
    }
}
