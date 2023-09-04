from typing import Any

CONST_DEFAULT_ARGS = {
    "owner": "airflow",
    "retries": 1,
}


def build_default_args(custom_args: dict[Any, Any] | None = None) -> dict[Any, Any]:
    actual_args = CONST_DEFAULT_ARGS.copy()

    if custom_args:
        return actual_args | custom_args

    return actual_args
