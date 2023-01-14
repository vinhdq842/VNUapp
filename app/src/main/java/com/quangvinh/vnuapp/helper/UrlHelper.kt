package com.quangvinh.vnuapp.helper

import java.net.URLEncoder

/**
 *
 * @author SOE
 */

fun encodeURL(str: String): String {
    return URLEncoder.encode(str, "UTF-8")
}

fun encodeRequestParams(params: List<Pair<String, String>>): String {
    var res = ""

    for (p in params) {
        res += encodeURL(p.first) + "=" + encodeURL(p.second) + "&"
    }

    return res.dropLast(1)
}