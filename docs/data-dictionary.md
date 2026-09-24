# Data Dictionary (`data-dictionary.md`)

> **CRITICAL RULE:** Always consult this document before creating or modifying any database table or field name. All database tables and columns must use `snake_case`.

## Tables Overview

### 1. `users`
Stores user profile and account preferences.
| Field Name | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | TEXT (UUID) | PRIMARY KEY | Unique identifier for the user |
| `display_name` | TEXT | NULLABLE | User display name |
| `created_at` | INTEGER | NOT NULL | Timestamp of account creation |
| `updated_at` | INTEGER | NOT NULL | Timestamp of last update |

### 2. `quotas`
Manages global and per-app usage limits.
| Field Name | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | TEXT (UUID) | PRIMARY KEY | Unique identifier for the quota rule |
| `app_package_name` | TEXT | NULLABLE | Package name of the target app (NULL if global quota) |
| `quota_limit_bytes` | INTEGER | NOT NULL | Maximum allowed data consumption in bytes |
| `period_type` | TEXT | NOT NULL | Quota period (`daily`, `monthly`) |
| `is_enabled` | INTEGER (BOOLEAN) | NOT NULL | Whether the quota rule is active (1 = true, 0 = false) |
| `created_at` | INTEGER | NOT NULL | Creation timestamp |
| `updated_at` | INTEGER | NOT NULL | Last updated timestamp |

### 3. `usage_logs`
Records historical data consumption metrics per app and connection type.
| Field Name | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | TEXT (UUID) | PRIMARY KEY | Unique identifier for the log entry |
| `app_package_name` | TEXT | NOT NULL | Package name associated with the usage |
| `bytes_rx` | INTEGER | NOT NULL | Bytes received (download) |
| `bytes_tx` | INTEGER | NOT NULL | Bytes transmitted (upload) |
| `connection_type` | TEXT | NOT NULL | Type of connection (`mobile`, `wifi`) |
| `timestamp` | INTEGER | NOT NULL | Epoch timestamp of the log record |

### 4. `app_settings`
Stores user preferences, theme settings, and security configurations.
| Field Name | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `key` | TEXT | PRIMARY KEY | Setting key identifier |
| `value` | TEXT | NOT NULL | Setting value (serialized) |
| `updated_at` | INTEGER | NOT NULL | Last update timestamp |
