WITH location AS (
    SELECT *
    FROM {{ ref('int_location') }}
)

SELECT
    concat_ws(' - ', location.region, location.nation) AS loc,
    count(*) AS count
FROM location
GROUP BY 1
