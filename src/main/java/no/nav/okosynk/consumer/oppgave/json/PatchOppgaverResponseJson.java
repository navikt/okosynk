package no.nav.okosynk.consumer.oppgave.json;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;
import java.util.Map;

public class PatchOppgaverResponseJson {
    private Integer feilet;
    private Integer suksess;
    private Integer totalt;
    private Map<String, List<Long>> data;

    public PatchOppgaverResponseJson() {
        //JAX-RS
    }

    public Integer getFeilet() {
        return feilet;
    }

    public void setFeilet(Integer feilet) {
        this.feilet = feilet;
    }

    public Integer getSuksess() {
        return suksess;
    }

    public void setSuksess(Integer suksess) {
        this.suksess = suksess;
    }

    public Integer getTotalt() {
        return totalt;
    }

    public void setTotalt(Integer totalt) {
        this.totalt = totalt;
    }

    public Map<String, List<Long>> getData() {
        return data;
    }

    public void setData(final Map<String, List<Long>> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("feilet", feilet)
                .append("suksess", suksess)
                .append("totalt", totalt)
                .append("data", data)
                .toString();
    }
}