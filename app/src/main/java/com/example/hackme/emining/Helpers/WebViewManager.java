package com.example.hackme.emining.Helpers;

public class WebViewManager {

    private String csscontent;

    public WebViewManager() {
        csscontent="<style>" +
                "    table{" +
                "        width: 100%;" +
                "        margin-top:1%;" +
                "        border: solid 1px #d2d2d2;" +
                "        -webkit-border-radius: 3px;" +
                "        -moz-border-radius: 3px;" +
                "         border-radius: 3px;" +
                "    }" +
                "    td,th{" +
                "        -webkit-border-radius: 3px;" +
                "        -moz-border-radius: 3px;" +
                "         border-radius: 3px;" +
                "}" +
                "    td,th{" +
                "        background-color:#CFCFCF;" +
                "        padding-top: 3%;" +
                "        padding-bottom: 3%;" +
                "        padding-left:1%;" +
                "        padding-right:1%;" +
                "    }" +
                ".div{" +
                "width: 98%;" +
                "padding-left: 1%;" +
                "padding-right: 1%;" +
                "padding-top: 1%;" +
                "padding-bottom: 1%;" +
                "color: #555252;" +
                "float:left;" +
                "}" +
                ".div_right{" +
                "width: 98%;" +
                "padding-left: 1%;" +
                "padding-right: 1%;" +
                "padding-top: 1%;" +
                "padding-bottom: 1%;" +
                "color: #555252;" +
                "float:right;" +
                "}" +
                ".m-top-1{" +
                "margin-top:1%;" +
                "}" +
                ".summary{" +
                "-webkit-border-radius: 3px;" +
                "-moz-border-radius: 3px;" +
                "border-radius: 3px;" +
                "padding-top:3%;" +
                "padding-bottom:3%;" +
                "border: solid 1px #d2d2d2;" +
                "}" +
                ".text_center{" +
                "text-align: center;" +
                "}" +
                ".text_left{" +
                "text-align: left;" +
                "}" +
                ".text_right{" +
                "text-align: right;" +
                "}" +
                ".text_bold{" +
                "font-weight: bold;" +
                "}" +
                ".draw_node{" +
                "background-color: #CFCFCF;" +
                "color: #555252;" +
                "font-size: small;" +
                "border-radius:4px;" +
                "}" +
                ".draw_node_trans{" +
                "color: #555252;" +
                "font-size: small;" +
                "border-radius:4px;" +
                "}" +
                ".content_center{" +
                "text-align: center;" +
                "}" +
                ".content_right{" +
                "text-align:right;" +
                "}" +
                ".content_left{" +
                "text-align:left;" +
                "}" +
                ".node_bg{" +
                "display: inline-block;" +
                "width: auto;" +
                "background-color: #009688;" +
                "margin:5px;" +
                "padding-left:4px;" +
                "padding-right:4px;" +
                "padding-top :4px;" +
                "padding-bottom:4px;" +
                "border-radius:4px;" +
                "}" +
                ".cer-btn{" +
                "border-radius:50%;" +
                "}" +
                ".bg_rul{"+
                "color:#009688;" +
                "}" +
                ".bg_r-trans{" +
                "color:#009688;" +
                "padding:3px 3px 3px 3px;" +
                "border-radius:2px;" +
                "border:solid 1px;" +
                "margin-top:3px;" +
                "}" +
                ".bg_r-base{" +
                "color:#FFFFFF;" +
                "background-color:#009688;" +
                "padding:3px 3px 3px 3px;" +
                "border-radius:2px;" +
                "margin-top:3px;" +
                "}" +
                ".bg_r-alert{" +
                "color:#FFFFFF;" +
                "background-color:#9a360c;" +
                "padding:3px 3px 3px 3px;" +
                "border-radius:2px;" +
                "margin-top:3px;" +
                "}" +
                ".bg_r-primary{" +
                "color:#FFFFFF;" +
                "background-color:#283593;" +
                "padding:3px 3px 3px 3px;" +
                "border-radius:2px;" +
                "margin-top:3px;" +
                "}" +
                ".inline{" +
                "display:inline-block;" +
                "}" +
                ".apriori-box{" +
                "background-color:#CFCFCF;" +
                "margin-top:1%;" +
                "margin-bottom:1%;" +
                "float:left;" +
                "border-radius:4px;" +
                "}" +
                ".text-title{" +
                "color:#009688;" +
                "margin-top:5px;" +
                "margin-bottom:5px;" +
                "margin-left:5px;" +
                "margin-right:5px;" +
                "float:left;" +
                "}" +
                ".text-content{" +
                "color:#555252;" +
                "margin-top:5px;" +
                "margin-bottom:5px;" +
                "margin-left:5px;" +
                "margin-right:5px;" +
                "float:left;" +
                "}" +
                "</style>";

    }


    public String htmlHead(String increateData){
        String head="<!Doctype html><html><head><meta charset='UTF-8' />";
        head+=increateData;
        head+="</head><body>";
        return head;
    }

    public String htmlFooter(){
        String head="</body></html>";
        return head;
    }

    public String getCSS(){
    return csscontent;
    }
}
