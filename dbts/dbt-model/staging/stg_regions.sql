WITH source AS (
    SELECT *
    FROM {{ source('tpch', 'region') }}
),

renamed AS (
    SELECT
        regionkey AS region_key,
        name AS name,
        comment AS comment

    FROM source
)

SELECT *
FROM renamed
