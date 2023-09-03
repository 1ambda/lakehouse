WITH source AS (
    SELECT *
    FROM {{ source('tpch', 'nation') }}
),

renamed AS (
    SELECT
        nationkey AS nation_key,
        name AS name,
        regionkey AS region_key,
        comment AS comment

    FROM source

    UNION ALL
    (

    -- make sure to test for dupes!!!
        SELECT
            nationkey AS nation_key,
            NULL AS name,
            NULL AS region_key,
            NULL AS comment

        FROM source LIMIT 1
    )
)

SELECT *
FROM renamed
