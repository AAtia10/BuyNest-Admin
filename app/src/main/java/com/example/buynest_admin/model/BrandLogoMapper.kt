package com.example.buynest_admin.model

import com.example.buynest_admin.R

    val brandLogos = mapOf(
    "adidas" to R.drawable.adidas_logo,
    "nike" to R.drawable.nike_logo,
    "puma" to R.drawable.puma_logo,
    "vans" to R.drawable.vans,
    "converse" to R.drawable.converse_logo,
    "asics tiger" to R.drawable.asics_tiger_logo,
    "dr martens" to R.drawable.dr_martens,
    "flex fit" to R.drawable.flexfit,
    "herschel" to R.drawable.herschel_logo,
    "palladium" to R.drawable.palladium_logo,
    "supra" to R.drawable.supra,
        "timberland" to R.drawable.timberland_logo,
)

fun getBrandLogo(vendor: String): Int {
    return brandLogos[vendor.lowercase()] ?: R.drawable.baseline_person_24
}