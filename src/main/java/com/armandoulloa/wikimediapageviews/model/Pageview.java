package com.armandoulloa.wikimediapageviews.model;

/**
 *
 * @author Armando
 */
public class Pageview {

    private String domainCode;
    private String pageTitle;
    private int countViews;

    public String getDomainCode() {
        return domainCode;
    }

    public void setDomainCode(String domainCode) {
        this.domainCode = domainCode;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public int getCountViews() {
        return countViews;
    }

    public void setCountViews(int countViews) {
        this.countViews = countViews;
    }

    public Pageview(String domainCode, String pageTitle, int countViews) {
        this.domainCode = domainCode;
        this.pageTitle = pageTitle;
        this.countViews = countViews;
    }

    @Override
    public String toString() {
        return "Views{"
                + "domainCode=" + domainCode
                + ", pageTitle=" + pageTitle
                + ", countViews=" + countViews
                + '}';
    }

}