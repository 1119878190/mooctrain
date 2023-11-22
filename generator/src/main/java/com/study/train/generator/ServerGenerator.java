package com.study.train.generator;

import com.study.train.generator.util.FreemarkerUtil;
import freemarker.template.TemplateException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ServerGenerator {

//    static String serverPath = "member/src/main/java/com/study/train/member/service/";
    static String serverPath = "[module]/src/main/java/com/study/train/[module]/service/";
    static String pomPath = "generator/pom.xml";


    public static void main(String[] args) throws DocumentException, IOException, TemplateException {

        // 读取 pom.xml 中指定的持久层xml文件路径
        String generatorPath = getGeneratorPath();

        // 比如generator-config-member.xml，得到module = member
        String module = generatorPath.replace("src/main/resources/generator-config-", "").replace(".xml", "");
        System.out.println("module: " + module);
        serverPath = serverPath.replace("[module]", module);
        new File(serverPath).mkdirs();
        System.out.println("servicePath: " + serverPath);


        // 读取生成器配置信息 获取表名和实体名
        Document document = new SAXReader().read("generator/" + generatorPath);
        Node table = document.selectSingleNode("//table");
        System.out.println(table);
        Node tableName = table.selectSingleNode("@tableName");
        Node domainObjectName = table.selectSingleNode("@domainObjectName");
        System.out.println(tableName.getText() + "/" + domainObjectName.getText());



        // 示例：表名 jiawa_test
        // Domain = JiawaTest
        String Domain = domainObjectName.getText();
        // domain = jiawaTest
        String domain = Domain.substring(0, 1).toLowerCase() + Domain.substring(1);
        // do_main = jiawa-test
        String do_main = tableName.getText().replaceAll("_", "-");


        // 组装参数
        Map<String, Object> param = new HashMap<>();
        param.put("module", module);
        param.put("Domain", Domain);
        param.put("domain", domain);
        param.put("do_main", do_main);


        // 生成
        FreemarkerUtil.initConfig("service.ftl");
        FreemarkerUtil.generator(serverPath + Domain + "Service.java",param);
    }


    /**
     * 获取pom中  configurationFile 的配置
     *
     * @return
     * @throws DocumentException
     */
    private static String getGeneratorPath() throws DocumentException {
        // 读取pom
        SAXReader saxReader = new SAXReader();
        Map<String, String> map = new HashMap<String, String>();
        map.put("pom", "http://maven.apache.org/POM/4.0.0");
        saxReader.getDocumentFactory().setXPathNamespaceURIs(map);
        Document document = saxReader.read(pomPath);
        Node node = document.selectSingleNode("//pom:configurationFile");
        System.out.println(node.getText());
        return node.getText();
    }
}
