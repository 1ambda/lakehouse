import os
from pathlib import Path

from airflow.models import DagBag

CONST_DAG_DIRECTORY = "dags"

CONST_AIRFLOW_HOME = os.environ.get("AIRFLOW_HOME", Path(__file__).parent.parent)


def test_no_import_errors() -> None:
    dag_folder = f"{CONST_AIRFLOW_HOME}/{CONST_DAG_DIRECTORY}"
    dag_bag = DagBag(dag_folder=dag_folder, include_examples=False)
    assert len(dag_bag.import_errors) == 0, "No Import Failures"



def test_retries_present() -> None:
    dag_folder = f"{CONST_AIRFLOW_HOME}/{CONST_DAG_DIRECTORY}"
    dag_bag = DagBag(dag_folder=dag_folder, include_examples=False)
    for dag in dag_bag.dags:
        retries = dag_bag.dags[dag].default_args.get("retries", [])
        error_msg = f"Retries are set for DAG {dag}"
        assert retries >= 1, error_msg
