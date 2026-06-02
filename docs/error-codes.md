# Mooder — Error Code Registry

All API error codes follow the pattern `MOODER_ERR_{NNN}`.
This file is the single source of truth — update it whenever a new code is introduced.

| Code | HTTP Status | Description |
|---|---|---|
| `MOODER_ERR_001` | 400 | Generic bad request |
| `MOODER_ERR_002` | 401 | Authentication required |
| `MOODER_ERR_003` | 403 | Insufficient permissions |
| `MOODER_ERR_004` | 404 | Resource not found |
| `MOODER_ERR_005` | 409 | Conflict — resource already exists |
| `MOODER_ERR_006` | 422 | Validation error (see `details` field) |
| `MOODER_ERR_007` | 429 | Rate limit exceeded |
| `MOODER_ERR_500` | 500 | Internal server error |
| `MOODER_ERR_503` | 503 | Service temporarily unavailable |

*Codes will be expanded per feature as development progresses.*
