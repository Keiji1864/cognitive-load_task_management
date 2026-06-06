package com.example.cognitask.presentation.util

fun String.normalizeYo(): String =
    this.trim().lowercase()
        .replace('ё', 'е')
        .replace('Ё', 'Е')