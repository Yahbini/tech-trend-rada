# api-gateway-service

REST API đọc index Elasticsearch `tech-trend-aggregated` (mỗi document = 1 công nghệ × 1 window 1h đã đóng).
Port mặc định `18082`. Mọi endpoint là `GET`, tham số truyền qua query string.

## GET /top

Bảng xếp hạng công nghệ của **một** window 1h.

| Param | Bắt buộc | Mặc định | Ghi chú |
|---|---|---|---|
| `category` | không | tất cả | 1 trong: `language, database, framework, ai-tool, devops, cloud, messaging, testing, tool, other` (chữ thường) |
| `window` | không | window mới nhất **của category đang lọc** | ISO-8601 UTC, là `window_start`, vd `2026-10-19T14:00:00Z` |
| `limit` | không | `10` | |
| `sortBy` | không | `count` | `count` hoặc `velocity` (không phân biệt hoa thường) |

Sort: field chính giảm dần → field còn lại giảm dần → `technologyId` tăng dần. `rank` bắt đầu từ 1.

```bash
curl "http://localhost:18082/top?category=language&limit=2"
```

```json
[
  {
    "canonicalName": "Go",
    "category": "language",
    "mentionCount": 20,
    "mentionVelocity": 0.8,
    "rank": 1,
    "technologyId": 9202,
    "windowStart": "2026-10-19T14:00:00Z"
  },
  {
    "canonicalName": "Python",
    "category": "language",
    "mentionCount": 20,
    "mentionVelocity": 0.2,
    "rank": 2,
    "technologyId": 9201,
    "windowStart": "2026-10-19T14:00:00Z"
  }
]
```

## GET /search

Tìm công nghệ theo tên. Mỗi công nghệ trả **1 dòng**.

| Param | Bắt buộc | Mặc định | Ghi chú |
|---|---|---|---|
| `q` | **có** | | Khớp một phần, không phân biệt hoa thường trên `canonicalName` (`rea` → React, `script` → JavaScript) |
| `window` | không | window mới nhất **của từng công nghệ** | Có truyền → chỉ lấy số liệu đúng giờ đó |
| `limit` | không | `10` | Đếm theo số công nghệ |

Sort: `windowStart` mới nhất → `mentionCount` giảm dần → `technologyId` tăng dần.
Mỗi dòng có thể thuộc một giờ khác nhau, xem `windowStart` để biết số liệu mới hay cũ.

```bash
curl "http://localhost:18082/search?q=script"
```

```json
[
  {
    "canonicalName": "JavaScript",
    "category": "language",
    "mentionCount": 7,
    "mentionVelocity": 0.0,
    "technologyId": 9104,
    "windowStart": "2026-10-19T13:00:00Z"
  },
  {
    "canonicalName": "TypeScript",
    "category": "language",
    "mentionCount": 5,
    "mentionVelocity": 0.0,
    "technologyId": 9103,
    "windowStart": "2026-10-19T12:00:00Z"
  }
]
```

## Lỗi

Không có kết quả → `200 []` (không phải lỗi).

Tham số sai → `400`, body chung cho mọi endpoint. Bot/FE hiển thị `message`; nhiều lỗi cùng lúc được nối bằng `; `.

| Request | `message` |
|---|---|
| `/top?category=abc` | `category must be one of: language, database, framework, ai-tool, devops, cloud, messaging, testing, tool, other` |
| `/search` hoặc `/search?q=` | `q must not be blank` |
| `/top?window=abc` | `window has invalid value 'abc'` |

```json
{
  "error": "Bad Request",
  "message": "category must be one of: language, database, framework, ai-tool, devops, cloud, messaging, testing, tool, other",
  "path": "/top",
  "status": 400,
  "timestamp": "2026-10-27T08:39:02.165069187Z"
}
```

Response mẫu ở trên lấy từ dữ liệu test (id 92xx/91xx), không phải dữ liệu thật.
