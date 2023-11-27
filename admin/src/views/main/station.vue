<template>
  <p>
    <a-space>
      <a-button type="primary" @click="handleQuery()">刷新</a-button>
      FreeMarker template error (DEBUG mode; use RETHROW in production!):
The following has evaluated to null or missing:
==> readOnly  [in template "vue.ftl" at line 5, column 13]

----
Tip: If the failing expression is known to legally refer to something that's sometimes null or missing, either specify a default value like myOptionalVar!myDefault, or use <#if myOptionalVar??>when-present<#else>when-missing</#if>. (These only cover the last step of the expression; to cover the whole expression, use parenthesis: (myOptionalVar.foo)!myDefault, (myOptionalVar.foo)??
----

----
FTL stack trace ("~" means nesting-related):
	- Failed at: #if !readOnly  [in template "vue.ftl" at line 5, column 7]
----

Java stack trace (for programmers):
----
freemarker.core.InvalidReferenceException: [... Exception message was already printed; see it above ...]
	at freemarker.core.InvalidReferenceException.getInstance(InvalidReferenceException.java:134)
	at freemarker.core.UnexpectedTypeException.newDescriptionBuilder(UnexpectedTypeException.java:85)
	at freemarker.core.UnexpectedTypeException.<init>(UnexpectedTypeException.java:48)
	at freemarker.core.NonBooleanException.<init>(NonBooleanException.java:47)
	at freemarker.core.Expression.modelToBoolean(Expression.java:195)
	at freemarker.core.Expression.evalToBoolean(Expression.java:178)
	at freemarker.core.Expression.evalToBoolean(Expression.java:163)
	at freemarker.core.NotExpression.evalToBoolean(NotExpression.java:34)
	at freemarker.core.ConditionalBlock.accept(ConditionalBlock.java:48)
	at freemarker.core.Environment.visit(Environment.java:347)
	at freemarker.core.Environment.visit(Environment.java:353)
	at freemarker.core.Environment.process(Environment.java:326)
	at freemarker.template.Template.process(Template.java:383)
	at com.study.train.generator.util.FreemarkerUtil.generator(FreemarkerUtil.java:36)
	at com.study.train.generator.ServerGenerator.genVue(ServerGenerator.java:112)
	at com.study.train.generator.ServerGenerator.main(ServerGenerator.java:94)
