/*
 * Copyright (c) 2010-2011, Monash e-Research Centre
 * (Monash University, Australia)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 	* Redistributions of source code must retain the above copyright
 * 	  notice, this list of conditions and the following disclaimer.
 * 	* Redistributions in binary form must reproduce the above copyright
 * 	  notice, this list of conditions and the following disclaimer in the
 * 	  documentation and/or other materials provided with the distribution.
 * 	* Neither the name of the Monash University nor the names of its
 * 	  contributors may be used to endorse or promote products derived from
 * 	  this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package edu.monash.merc.struts2.action;

import edu.monash.merc.common.page.Pagination;
import edu.monash.merc.config.AppPropSettings;
import edu.monash.merc.domain.Data;
import edu.monash.merc.domain.IFNType;
import edu.monash.merc.dto.RangeCondition;
import edu.monash.merc.dto.SearchBean;
import edu.monash.merc.exception.DCConfigException;
import edu.monash.merc.service.SearchDataService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.aspectj.weaver.patterns.ThisOrTargetAnnotationPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * SearchAction Action class
 *
 * @author Simon Yu - Xiaoming.Yu@monash.edu
 * @version 2.0
 */
@Scope("prototype")
@Controller("search.searchAction")
public class SearchAction extends DMBaseAction {

    @Autowired
    private SearchDataService searchDataService;

    /**
     * pre-defined interferon type
     */
    private Map<String, String> ifnTypeMap = new LinkedHashMap<String, String>();

    /**
     * interferon sub type loaded from db
     */
    private Map<String, String> ifnSubTypeMap = new LinkedHashMap<String, String>();

    /**
     * pre-defined experimental conditions
     */
    private Map<String, String> vivoVitroMap = new LinkedHashMap<String, String>();


    /**
     * species map loaded from db
     */
    private Map<String, String> speciesMap = new LinkedHashMap<String, String>();

    /**
     * system map loaded from db
     */
    private Map<String, String> systemMap = new LinkedHashMap<String, String>();


    /**
     * organ maps loaded from db
     */
    private Map<String, String> organMap = new LinkedHashMap<String, String>();


    /**
     * cell maps loaded from db
     */
    private Map<String, String> cellMap = new LinkedHashMap<String, String>();


    /**
     * cell lines loaded from db
     */
    private Map<String, String> cellLineMap = new LinkedHashMap<String, String>();


    /**
     * pre-defined any or by range searching
     */
    private Map<String, String> anyOrRanges = new LinkedHashMap<String, String>();

    /**
     * pre-defined up or down condition for fold change
     */
    private Map<String, String> upDown = new LinkedHashMap<String, String>();

    /**
     * pre-defined normal or abnormal radio options
     */
    private Map<String, String> variationMap = new LinkedHashMap<String, String>();


    /**
     * abnormal variation values loaded from db
     */
    private Map<String, String> abnormalMap = new LinkedHashMap<String, String>();


    /**
     * search bean
     */
    private SearchBean searchBean;

    /**
     * check the search condition is loaded or not flag
     */
    private boolean conditionLoaded;

    /**
     * searched flag
     */
    private boolean searched;

    /**
     * search result dataset
     */
    private Pagination<Data> dataPagination;

    private String viewDsAct;

    /**
     * genbank link
     */
    private String genBankLink;

    /**
     * ensembl link
     */
    private String ensemblLink;

    /**
     * Logger
     */
    private Logger logger = Logger.getLogger(this.getClass().getName());

    public void setSearchDataService(SearchDataService searchDataService) {
        this.searchDataService = searchDataService;
    }

    @PostConstruct
    public void initSearchConds() {
        // load the out site  resource links
        loadResourceLinks();

        //pre-define the interferon type
        populateINFTypes();

        //pre-define the vivo vitro type
        populateVivoVitros();

        // pre-define the any or by range
        populateAnyOrByRanges();

        //pre-define the normal or abnormal
        populateVariations();

        //pre-define the up or down option
        populateUpDown();

        //for pagination
        initDataPagination();

        //set the default search pagination params
        setDefaultPageParams();

        try {
            //load species
            loadSpecies();

            //load system
            loadSystem();

            //load organs, cell, cell lines map
            loadMutipleChoices();

            //load abnormal values
            loadAbnormals();
            //set condition loaded true
            conditionLoaded = true;
        } catch (Exception ex) {
            logger.error("Failed to load the pre-defined search condition, " + ex);
            conditionLoaded = false;
        }
    }

    private void loadResourceLinks() {
        this.ensemblLink = this.appSetting.getPropValue(AppPropSettings.ENSEMBL_SUMMARY_LINK);
        this.genBankLink = this.appSetting.getPropValue(AppPropSettings.GENBANK_SUMMARY_LINK);
    }

    private void populateINFTypes() {
        ifnTypeMap.put("I", "I");
        ifnTypeMap.put("II", "II");
        ifnTypeMap.put("III", "III");
    }

    private void populateVivoVitros() {
        vivoVitroMap.put("In Vivo", "In Vivo");
        vivoVitroMap.put("In Vitro", "In Vitro");
    }

    //radio options: any or by range
    private void populateAnyOrByRanges() {
        anyOrRanges.put("any", "Any");
        anyOrRanges.put("byrange", "By Range");
    }

    //folder change up or down option
    private void populateUpDown() {
        upDown.put("up", "Up");
        upDown.put("down", "Down");
    }

    ///normal or abnormal radio option
    private void populateVariations() {
        variationMap.put("any", "Any");
        variationMap.put("Normal", "Normal");
        variationMap.put("Abnormal", "Abnormal");
    }

    private void loadSpecies() {
        List<String> speciesList = this.dmService.getFactorValuesByFactorName("Species");
        for (String species : speciesList) {
            speciesMap.put(species, species);
        }
    }

    private void loadSystem() {
        List<String> systemNames = this.dmService.getFactorValuesByFactorName("System");
        for (String system : systemNames) {
            systemMap.put(system, system);
        }
    }

    private void loadMutipleChoices() {
        //load the organs if any
        List<String> organs = this.dmService.getFactorValuesByFactorName("Organ");
        if (organs != null) {
            for (String organ : organs) {
                organMap.put(organ, organ);
            }
        }

        //load the cells if any
        List<String> cells = this.dmService.getFactorValuesByFactorName("Cell");
        if (cells != null) {
            for (String cell : cells) {
                cellMap.put(cell, cell);
            }
        }

        //load the cell lines if any
        List<String> celllines = this.dmService.getFactorValuesByFactorName("Cell Lines");
        if (celllines != null) {
            for (String cline : celllines) {
                cellLineMap.put(cline, cline);
            }
        }
    }

    private void loadAbnormals() {
        //load the abnormal variations if any
        List<String> abnormals = this.dmService.getAbnormalFactors();
        if (abnormals != null) {
            for (String abn : abnormals) {
                abnormalMap.put(abn, abn);
            }
        }
    }


    public String showSearch() {
        try {
            user = getCurrentUser();
            //remove the previous search conditions if any
            removeFromSession(ActionConts.SEARCH_CON_KEY);
            //create a search bean
            searchBean = new SearchBean();
            //set the default  values for multiple options
            setMultipleOptionsDefaultValues();
            //check the search conditions are loaded or not
            if (!conditionLoaded) {
                new DCConfigException("failed to load the search conditions");
            }
        } catch (Exception ex) {
            addActionError(getText("data.search.show.search.failed"));
            return ERROR;
        }
        return SUCCESS;
    }

    private void setMultipleOptionsDefaultValues() {
        searchBean.getOrgans().add("-1");
        searchBean.getCells().add("-1");
        searchBean.getCellLines().add("-1");
    }


    @SuppressWarnings("unchecked")
    public String search() {
        try {
            //get the logged in user if existed
            user = getCurrentUser();
            if (user != null) {
                viewDsAct = ActionConts.VIEW_DATASET_ACTION;
            } else {
                viewDsAct = ActionConts.VIEW_PUB_DATASET_ACTION;
            }
            //validation failed
            if (!validConds()) {
                //sub type post process
                subTypePostProcess();
                return ERROR;
            }
            //query the data by pagination
            dataPagination = this.searchDataService.search(searchBean, pageNo, pageSize, orderBy, orderByType);
            //set the searched flag as true
            searched = true;
            //sub type post process
            subTypePostProcess();
            storeInSession(ActionConts.SEARCH_CON_KEY, searchBean);
        } catch (Exception ex) {
            logger.error(ex);
            addActionError(getText("data.search.data.failed"));
            return ERROR;
        }
        return SUCCESS;
    }

    private boolean validConds() {
        //check the searchBean first
        if (searchBean == null) {
            searchBean = (SearchBean) findFromSession(ActionConts.SEARCH_CON_KEY);
            if (searchBean == null) {
                searchBean = new SearchBean();
            }
        }
        boolean defaultSearch = searchBean.isDefaultSearchCondition();
        if (user == null && defaultSearch) {
            addFieldError("onecondreq", getText("data.search.at.least.one.condition.required"));
            return false;
        }

        boolean hasError = false;
        RangeCondition doseRangeCond = searchBean.getDoseRangeCondition();
        if (doseRangeCond.isRangeProvided()) {
            double fromDose = doseRangeCond.getFromValue();
            double toDose = doseRangeCond.getToValue();
            if (fromDose < 0) {
                addFieldError("fromDose", getText("data.search.invalid.dose.from.value"));
                hasError = true;
            }
            if (toDose < 0) {
                addFieldError("toDose", getText("data.search.invalid.dose.to.value"));
                hasError = true;
            }
            if (fromDose > 0 && toDose > 0 & (toDose < fromDose)) {
                addFieldError("toDose", getText("data.search.invalid.dose.range"));
                hasError = true;
            }
        }

        RangeCondition ttimeRange = searchBean.getTimeRangeCondition();
        if (ttimeRange.isRangeProvided()) {
            double fromTime = ttimeRange.getFromValue();
            double toTime = ttimeRange.getToValue();
            if (fromTime < 0) {
                addFieldError("fromTime", getText("data.search.invalid.ttime.from.value"));
                hasError = true;
            }
            if (toTime < 0) {
                addFieldError("toTime", getText("data.search.invalid.ttime.to.value"));
                hasError = true;
            }
            if (fromTime > 0 && toTime > 0 & (toTime < fromTime)) {
                addFieldError("fromToTime", getText("data.search.invalid.ttime.range"));
                hasError = true;
            }
        }
        if (searchBean.isUpProvided()) {
            double upValue = searchBean.getUpValue();
            double downValue = searchBean.getDownValue();
            if (searchBean.isUpProvided() && upValue < 1) {
                addFieldError("upValue", getText("data.search.invalid.foldchange.up.value"));
                hasError = true;
            }
            if (searchBean.isDownProvided() && downValue < 1) {
                addFieldError("downValue", getText("data.search.invalid.foldchange.down.value"));
                hasError = true;
            }
        }
        //if validation has an error, just return false
        if (hasError) {
            return false;
        }
        return true;
    }

    private void subTypePostProcess() {
        //Load the interferon subtypes based on the type selected
        //and if the type is not all value -1, then loaded the subtype again
        if (searchBean.getIfnType() != "-1") {
            List<IFNType> ifnTypes = this.dmService.getIfnTypes(searchBean.getIfnType());
            if (ifnTypes != null) {
                for (IFNType ifnType : ifnTypes) {
                    ifnSubTypeMap.put(ifnType.getSubTypeName(), ifnType.getSubTypeName());
                }
            }
        }
    }

    //initialize the pagination parameters
    protected void initDataPagination() {
        // page size per page values
        pageSizeMap.put(20, 20);
        pageSizeMap.put(30, 30);
        pageSizeMap.put(50, 50);
        pageSizeMap.put(100, 100);
        pageSizeMap.put(150, 150);
        pageSizeMap.put(200, 200);

        // orderby values
        orderByMap.put("dataset", "dataset");
        orderByMap.put("foldchange", "fold change");
        orderByMap.put("ifntype", "interferon type");
        orderByMap.put("ttime", "treatment time");
        orderByMap.put("genesymbol", "gene symbol");
        orderByMap.put("genbank", "genbank id");
        orderByMap.put("ensemblid", "ensembl id");
        orderByMap.put("probeid", "probe id");

        // orderby type values
        orderByTypeMap.put("ASC", "asc");
        orderByTypeMap.put("DESC", "desc");
    }

    //set the default pagination parameters
    protected void setDefaultPageParams() {
        pageLink = "search/search.jspx";
        pageSuffix = ActionConts.PAGINATION_SUFFUX;
        if (StringUtils.isBlank(orderBy)) {
            orderBy = "dataset";
        }
        if (StringUtils.isBlank(orderByType)) {
            orderByType = ActionConts.DESC_SORT_TYPE;
        }
        if (pageNo == 0) {
            pageNo = 1;
        }
        if (pageSize == 0) {
            pageSize = 30;
        }
    }

    /**
     * Ajax call for finding the interferon subtype based on which interferon type is selected.
     *
     * @return a success string if succeed
     */
    public String findSubType() {
        try {
            List<IFNType> ifnTypes = this.dmService.getIfnTypes(searchBean.getIfnType());
            if (ifnTypes != null) {
                for (IFNType ifnType : ifnTypes) {
                    ifnSubTypeMap.put(ifnType.getSubTypeName(), ifnType.getSubTypeName());
                }
            }
        } catch (Exception ex) {
            logger.error(ex);
            return ERROR;
        }
        return SUCCESS;
    }

    public Map<String, String> getIfnTypeMap() {
        return ifnTypeMap;
    }

    public void setIfnTypeMap(Map<String, String> ifnTypeMap) {
        this.ifnTypeMap = ifnTypeMap;
    }

    public Map<String, String> getIfnSubTypeMap() {
        return ifnSubTypeMap;
    }

    public void setIfnSubTypeMap(Map<String, String> ifnSubTypeMap) {
        this.ifnSubTypeMap = ifnSubTypeMap;
    }

    public Map<String, String> getVivoVitroMap() {
        return vivoVitroMap;
    }

    public void setVivoVitroMap(Map<String, String> vivoVitroMap) {
        this.vivoVitroMap = vivoVitroMap;
    }

    public Map<String, String> getSpeciesMap() {
        return speciesMap;
    }

    public void setSpeciesMap(Map<String, String> speciesMap) {
        this.speciesMap = speciesMap;
    }

    public Map<String, String> getSystemMap() {
        return systemMap;
    }

    public void setSystemMap(Map<String, String> systemMap) {
        this.systemMap = systemMap;
    }

    public Map<String, String> getOrganMap() {
        return organMap;
    }

    public void setOrganMap(Map<String, String> organMap) {
        this.organMap = organMap;
    }

    public Map<String, String> getCellMap() {
        return cellMap;
    }

    public void setCellMap(Map<String, String> cellMap) {
        this.cellMap = cellMap;
    }

    public Map<String, String> getCellLineMap() {
        return cellLineMap;
    }

    public void setCellLineMap(Map<String, String> cellLineMap) {
        this.cellLineMap = cellLineMap;
    }

    public Map<String, String> getAnyOrRanges() {
        return anyOrRanges;
    }

    public void setAnyOrRanges(Map<String, String> anyOrRanges) {
        this.anyOrRanges = anyOrRanges;
    }

    public Map<String, String> getUpDown() {
        return upDown;
    }

    public void setUpDown(Map<String, String> upDown) {
        this.upDown = upDown;
    }

    public Map<String, String> getVariationMap() {
        return variationMap;
    }

    public void setVariationMap(Map<String, String> variationMap) {
        this.variationMap = variationMap;
    }

    public Map<String, String> getAbnormalMap() {
        return abnormalMap;
    }

    public void setAbnormalMap(Map<String, String> abnormalMap) {
        this.abnormalMap = abnormalMap;
    }

    public SearchBean getSearchBean() {
        return searchBean;
    }

    public void setSearchBean(SearchBean searchBean) {
        this.searchBean = searchBean;
    }

    public boolean isSearched() {
        return searched;
    }

    public void setSearched(boolean searched) {
        this.searched = searched;
    }

    public Pagination<Data> getDataPagination() {
        return dataPagination;
    }

    public void setDataPagination(Pagination<Data> dataPagination) {
        this.dataPagination = dataPagination;
    }

    public String getViewDsAct() {
        return viewDsAct;
    }

    public void setViewDsAct(String viewDsAct) {
        this.viewDsAct = viewDsAct;
    }

    public boolean isConditionLoaded() {
        return conditionLoaded;
    }

    public void setConditionLoaded(boolean conditionLoaded) {
        this.conditionLoaded = conditionLoaded;
    }

    public String getGenBankLink() {
        return genBankLink;
    }

    public void setGenBankLink(String genBankLink) {
        this.genBankLink = genBankLink;
    }

    public String getEnsemblLink() {
        return ensemblLink;
    }

    public void setEnsemblLink(String ensemblLink) {
        this.ensemblLink = ensemblLink;
    }
}
