with source as (select *
                from {{ source('tpch', 'nation') }}),

     renamed as (select nationkey as nation_key,
                        name      as name,
                        regionkey as region_key,
                        comment   as comment

                 from source

                 union all
                 (

                     -- make sure to test for dupes!!!
                     select nationkey as nation_key,
                            null      as name,
                            null      as region_key,
                            null      as comment

                     from source limit 1))

select *
from renamed
