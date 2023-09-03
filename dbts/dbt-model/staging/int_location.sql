WITH nation AS (
    SELECT *
    FROM {{ ref('stg_nations') }}
),

region AS (
    SELECT *
    FROM {{ ref('stg_regions') }}
),

final AS (
    SELECT
        nation.nation_key,
        nation.name AS nation,
        nation.region_key,
        region.name AS region
    FROM nation
    INNER JOIN region
        ON nation.region_key = region.region_key
)

SELECT *
FROM final
