/*
 * WiFiAnalyzer
 * Copyright (C) 2015 - 2022 VREM Software Development <VREMSoftwareDevelopment@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.example.speedtest_rework.common.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import com.example.speedtest_rework.R
import java.util.*


private object LocaleUtils {
    @SuppressLint("ConstantLocale")
    val defaultLocale: Locale = Locale.getDefault()
    val countryCodes: Set<String> = Locale.getISOCountries().toSet()
    val availableLocales: List<Locale> =
        Locale.getAvailableLocales().filter { countryCodes.contains(it.country) }

    @SuppressLint("ConstantLocale")
    val countriesLocales: SortedMap<String, Locale> =
        availableLocales
            .associateBy { it.country.toCapitalize(Locale.getDefault()) }
            .toSortedMap()
    val supportedLocales: List<Locale> = setOf(
        ENGLISH,
        JAPANESE,
        KOREAN,
        FRENCH,
        ITALIA,
        GERMAN,
        VIETNAM
    )
        .toList()


    val supportLanguages: List<Pair<Int, Int>> = setOf(
        Pair(R.string.ENGLISH, R.drawable.ic_flag_england),
        Pair(R.string.JAPANESE, R.drawable.ic_flag_japan),
        Pair(R.string.KOREAN, R.drawable.ic_flag_korea),
        Pair(R.string.FRENCH, R.drawable.ic_flag_france),
        Pair(R.string.ITALIA, R.drawable.ic_flag_italia),
        Pair(R.string.GERMAN, R.drawable.ic_flag_germany),
        Pair(R.string.VIETNAM, R.drawable.ic_flag_vietnam)
    ).toList()
}

val VIETNAM = Locale("vi")
val ITALIA = Locale("it")
val ENGLISH = Locale("en")
val JAPANESE = Locale("ja")
val KOREAN = Locale("ko")
val FRENCH = Locale("fr")
val GERMAN = Locale("de")


private const val SEPARATOR: String = "_"

fun findByCountryCode(countryCode: String): Locale =
    LocaleUtils.availableLocales
        .find { countryCode.toCapitalize(Locale.getDefault()) == it.country }
        ?: LocaleUtils.defaultLocale

fun allCountries(): List<Locale> = LocaleUtils.countriesLocales.values.toList()

fun findByLanguageTag(languageTag: String): Locale {
    val languageTagPredicate: (Locale) -> Boolean = {
        val locale: Locale = fromLanguageTag(languageTag)
        it.language == locale.language
    }
    return LocaleUtils.supportedLocales.find(languageTagPredicate) ?: LocaleUtils.defaultLocale
}

fun supportedLanguages(): List<Locale> = LocaleUtils.supportedLocales
fun supportDisplayLang(): List<Pair<Int, Int>> = LocaleUtils.supportLanguages

fun defaultCountryCode(): String = LocaleUtils.defaultLocale.country

fun defaultLanguageTag(): String = toLanguageTag(LocaleUtils.defaultLocale)

fun toLanguageTag(locale: Locale): String = locale.language + SEPARATOR + locale.country

private fun fromLanguageTag(languageTag: String): Locale {
    val codes: Array<String> = languageTag.split(SEPARATOR).toTypedArray()
    return when (codes.size) {
        1 -> Locale(codes[0])
        2 -> Locale(codes[0], codes[1].toCapitalize(Locale.getDefault()))
        else -> LocaleUtils.defaultLocale
    }
}
