<div class="result_title_div">
    Search Results
</div>
<div class="data_header_div">
    <span class="name_title">Found a total of <font color="green"> ${genePagination.totalRecords} </font> Data</span>

<@s.if test="%{genePagination.totalRecords >0 }">
    <div class="export_div">
        Save as a CSV file <a
            href="${base}/search/exportCsvFile.jspx?maxRecords=<@s.property value='genePagination.totalRecords' />&orderBy=${orderBy}&orderByType=${orderByType}">
        <img src="${base}/images/export.png" class="search_ctip_image" id="export_csv"/></a>
    </div>
</@s.if>
    <div style="clear:both"></div>
    <!-- page sorting block -->
    <div class="msg_content">
        <a href="${base}/${pageLink}${pageSuffix}<@s.property value='genePagination.pageNo' />" class="page_url"></a>
    </div>
    <br/>
<#include "../pagination/pagination_header.ftl"/>
</div>

<div class="search_table_div">
    <table class="search_result_tab">
        <thead>
        <tr class="search_result_header">
            <td align="center">Ensembl Id</td>
            <td align="center">Gene Name</td>
            <td align="center">Description</td>
            <td align="center">Entrez</td>
            <td align="center">Genbank</td>
            <td align="center">UniGene</td>
        </tr>
        </thead>
        <tbody>
        <@s.iterator status="geneStat" value="genePagination.pageResults" id="geneResult" >
        <tr>
            <td align="center"><@s.property value='#geneResult.ensgAccession' /></td>
            <td align="center"><@s.property value="#geneResult.geneName" /></td>
            <td align="center"><@s.property value="#geneResult.description" /></td>
            <td align="center"><@s.property value="#geneResult.entrezId" /></td>
            <td align="center"><@s.property value="#geneResult.genbankId" /></td>
            <td align="center"><@s.property value="#geneResult.unigene" /></td>
        </tr>
        </@s.iterator>
        </tbody>
    </table>
</div>
<br/>
<#include "../pagination/search_gene_page_style.ftl" />