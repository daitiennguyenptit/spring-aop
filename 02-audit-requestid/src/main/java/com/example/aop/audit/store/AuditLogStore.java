package com.example.aop.audit.store;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Component;

/**
 * Kho luu audit (demo: in-memory). Thuc te: bang AUDIT_LOG, Kafka topic, ELK...
 * Nen ghi bat dong bo / outbox de khong lam cham nghiep vu.
 */
@Component
public class AuditLogStore {

    private final List<AuditRecord> records = new CopyOnWriteArrayList<>();

    public void save(AuditRecord auditRecord) {
        records.add(auditRecord);
    }

    public List<AuditRecord> findAll() {
        return List.copyOf(records);
    }

    public List<AuditRecord> findByRequestId(String requestId) {
        return records.stream().filter(r -> requestId.equals(r.requestId())).toList();
    }

    public void clear() {
        records.clear();
    }
}
