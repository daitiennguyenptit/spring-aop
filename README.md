# spring-aop

Tổng hợp kiến thức Spring AOP theo bài toán thực tế. Mỗi bài toán là một module demo chạy được, có test kèm theo.

**Mở `index.html` trong trình duyệt để xem trang tổng hợp.**

| Module | Bài toán | Port |
|---|---|---|
| `01-aop-basics` | 5 loại advice, thứ tự, pointcut, proxy | console |
| `02-audit-requestid` | ★ Audit log từng hàm có requestId, không truyền qua tham số (kể cả @Async) | 8082 |
| `03-performance-monitoring` | Đo thời gian, cảnh báo method chậm | 8083 |
| `04-retry` | Retry lỗi tạm thời, exponential backoff | 8084 |
| `05-authorization` | Phân quyền bằng `@RequiresRole` | 8085 |
| `06-pitfalls` | Self-invocation, final method, `new` và cách sửa | console |

```bash
mvn test                                   # chạy toàn bộ 26 test
mvn -pl 02-audit-requestid spring-boot:run # chạy một demo
```

Yêu cầu: JDK 21+, Maven 3.9+.
