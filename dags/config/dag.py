from typing import Any, Optional, Dict

CONST_DEFAULT_ARGS = {
    "owner": "airflow",
    "retries": 1,
}


def build_default_args(custom_args: Optional[Dict[Any, Any]] = None) -> Dict[Any, Any]:
    actual_args = CONST_DEFAULT_ARGS.copy()

    if custom_args:
        return actual_args | custom_args

    return actual_args
