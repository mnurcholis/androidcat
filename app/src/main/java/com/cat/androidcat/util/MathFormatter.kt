package com.cat.androidcat.util

object MathFormatter {
    private val superscripts = mapOf(
        '0' to "⁰", '1' to "¹", '2' to "²", '3' to "³", '4' to "⁴",
        '5' to "⁵", '6' to "⁶", '7' to "⁷", '8' to "⁸", '9' to "⁹",
        '+' to "⁺", '-' to "⁻", '=' to "⁼", '(' to "⁽", ')' to "⁾", 'n' to "ⁿ", 'x' to "ˣ"
    )

    private val subscripts = mapOf(
        '0' to "₀", '1' to "₁", '2' to "₂", '3' to "₃", '4' to "₄",
        '5' to "₅", '6' to "₆", '7' to "₇", '8' to "₈", '9' to "₉",
        '+' to "₊", '-' to "₋", '=' to "₌", '(' to "₍", ')' to "₎"
    )

    fun format(raw: String?): String {
        if (raw.isNullOrBlank()) return ""
        var text: String = raw

        // 1. Escaped symbols
        text = text.replace("\\%", "%")
        text = text.replace("\\$", "$")
        text = text.replace("\\,", ",")

        // 2. \text{...} or \mathrm{...} -> inner text
        text = Regex("""\\(?:text|mathrm|mathbf)\s*\{([^}]*)\}""").replace(text) { match ->
            match.groupValues[1]
        }

        // 3. Parentheses & Brackets
        text = Regex("""\\left\s*\(""").replace(text, "(")
        text = Regex("""\\right\s*\)""").replace(text, ")")
        text = Regex("""\\left\s*\[""").replace(text, "[")
        text = Regex("""\\right\s*\]""").replace(text, "]")
        text = Regex("""\\left\s*\\\{""").replace(text, "{")
        text = Regex("""\\right\s*\\\}""").replace(text, "}")

        // 4. Fractions: \frac{a}{b} -> (a / b)
        val fracRegex = Regex("""\\frac\s*\{([^{}]+)\}\s*\{([^{}]+)\}""")
        while (fracRegex.containsMatchIn(text)) {
            text = fracRegex.replace(text) { match ->
                val num = match.groupValues[1].trim()
                val den = match.groupValues[2].trim()
                "($num / $den)"
            }
        }

        // 5. Roots: \sqrt[3]{x} -> ∛(x), \sqrt[n]{x} -> ⁿ√(x), \sqrt{x} -> √(x)
        text = Regex("""\\sqrt\s*\[3\]\s*\{([^{}]+)\}""").replace(text) { match ->
            "∛(${match.groupValues[1].trim()})"
        }
        text = Regex("""\\sqrt\s*\[([0-9]+)\]\s*\{([^{}]+)\}""").replace(text) { match ->
            val n = match.groupValues[1].map { c -> superscripts[c] ?: c.toString() }.joinToString("")
            "$n√(${match.groupValues[2].trim()})"
        }
        text = Regex("""\\sqrt\s*\{([^{}]+)\}""").replace(text) { match ->
            "√(${match.groupValues[1].trim()})"
        }

        // 6. Common Math Operators
        text = text.replace(Regex("""\\times\b"""), "×")
        text = text.replace(Regex("""\\div\b"""), "÷")
        text = text.replace(Regex("""\\cdot\b"""), "·")
        text = text.replace(Regex("""\\pm\b"""), "±")
        text = text.replace(Regex("""\\mp\b"""), "∓")
        text = text.replace(Regex("""\\le(?:q)?\b"""), "≤")
        text = text.replace(Regex("""\\ge(?:q)?\b"""), "≥")
        text = text.replace(Regex("""\\ne(?:q)?\b"""), "≠")
        text = text.replace(Regex("""\\approx\b"""), "≈")
        text = text.replace(Regex("""\\infty\b"""), "∞")
        text = text.replace(Regex("""\\pi\b"""), "π")
        text = text.replace(Regex("""\\alpha\b"""), "α")
        text = text.replace(Regex("""\\beta\b"""), "β")
        text = text.replace(Regex("""\\theta\b"""), "θ")
        text = text.replace(Regex("""\\Delta\b"""), "Δ")

        // 7. Superscripts: ^{2} or ^2
        text = Regex("""\^\{([0-9+\-=()nx]+)\}""").replace(text) { match ->
            match.groupValues[1].map { c -> superscripts[c] ?: c.toString() }.joinToString("")
        }
        text = Regex("""\^([0-9nx])""").replace(text) { match ->
            superscripts[match.groupValues[1][0]] ?: match.groupValues[1]
        }

        // 8. Subscripts: _{1} or _1
        text = Regex("""_\{([0-9+\-=()]+)\}""").replace(text) { match ->
            match.groupValues[1].map { c -> subscripts[c] ?: c.toString() }.joinToString("")
        }
        text = Regex("""_([0-9])""").replace(text) { match ->
            subscripts[match.groupValues[1][0]] ?: match.groupValues[1]
        }

        // 9. Remove TeX math mode delimiters $
        text = text.replace("$", "")

        // 10. Clean up extra spaces around parentheses and operators
        text = text.replace(Regex("""\(\s+"""), "(")
        text = text.replace(Regex("""\s+\)"""), ")")
        text = text.replace(Regex("""[ \t]+"""), " ")

        return text.trim()
    }
}
