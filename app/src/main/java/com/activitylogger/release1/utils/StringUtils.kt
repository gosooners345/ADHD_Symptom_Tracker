package com.activitylogger.release1.utils


class StringUtils
{
  companion object
  {
    fun sanitizeSearchQuery(query: String?): String
    {
      if (query == null)
      {
        return ""
      }
      val queryWithEscapedQuotes =
        query.replace(Regex.fromLiteral("\""), "\"\"")
      return "*\"$queryWithEscapedQuotes\"*"
    }
  }
}