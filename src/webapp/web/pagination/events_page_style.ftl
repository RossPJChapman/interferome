<div class="page_style">
	<span class="total">Total ${eventPagination.totalPages} Pages</span>
<#if eventPagination.firstPage>
	<span class="disabled"> <img src="${base}/images/dis_first.png" class="page_style_img" /> Prev </span>
<#else>
	<a href="${base}/${pageLink}${pageSuffix}1&pageSize=${pageSize}&orderBy=${orderBy}&orderByType=${orderByType}"> <img src="${base}/images/first.png" class="page_style_img" /> First </a>
	<a href="${base}/${pageLink}${pageSuffix}${eventPagination.prevPage}&pageSize=${pageSize}&orderBy=${orderBy}&orderByType=${orderByType}"> <img src="${base}/images/prev.png" class="page_style_img" /> Prev </a>
</#if>
<#if eventPagination.pageNo-5 gt 1>
	<#if eventPagination.totalPages gt eventPagination.pageNo+4>
		<#list eventPagination.pageNo-5..eventPagination.pageNo+4 as i>
			<#if i == eventPagination.pageNo>
				<span class="current">${i}</span>
			<#else>
				<a href="${base}/${pageLink}${pageSuffix}<#if i gt 0>${i}&pageSize=${pageSize}&orderBy=${orderBy}&orderByType=${orderByType}</#if>">${i}</a>
			</#if>
		</#list>
	<#else>
		<#list eventPagination.totalPages-9..eventPagination.totalPages as i>
			<#if i == eventPagination.pageNo>
				<span class="current">${i}</span>
			<#else>
				<a href="${base}/${pageLink}${pageSuffix}<#if i gt 0>${i}&pageSize=${pageSize}&orderBy=${orderBy}&orderByType=${orderByType}</#if>">${i}</a>
			</#if>
		</#list>
	</#if>
<#else>
	<#if eventPagination.totalPages gt 10>
		<#list 1..10 as i>
			<#if i == eventPagination.pageNo>
				<span class="current">${i}</span>
			<#else>
				<a href="${base}/${pageLink}${pageSuffix}<#if i gt 0>${i}&pageSize=${pageSize}&orderBy=${orderBy}&orderByType=${orderByType}</#if>">${i}</a>
			</#if>
		</#list>
	<#else>
		<#list 1..eventPagination.totalPages as i>
			<#if i == eventPagination.pageNo>
				<span class="current">${i}</span>
			<#else>
				<a href="${base}/${pageLink}${pageSuffix}<#if i gt 0>${i}&pageSize=${pageSize}&orderBy=${orderBy}&orderByType=${orderByType}</#if>">${i}</a>
			</#if>
		</#list>
	</#if>
</#if>
<#if eventPagination.lastPage>	
	<span class="disabled"> Next <img src="${base}/images/dis_next.png" class="page_style_img" /> </span><span class="disabled"> Last <img src="${base}/images/dis_last.png" class="page_style_img" /> </span>
<#else>
	<a href="${base}/${pageLink}${pageSuffix}${eventPagination.nextPage}&pageSize=${pageSize}&orderBy=${orderBy}&orderByType=${orderByType}"> Next <img src="${base}/images/next.png" class="page_style_img" /> </a>
	<a href="${base}/${pageLink}${pageSuffix}${eventPagination.totalPages}&pageSize=${pageSize}&orderBy=${orderBy}&orderByType=${orderByType}"> Last <img src="${base}/images/last.png" class="page_style_img" /> </a>
</#if>
</div>