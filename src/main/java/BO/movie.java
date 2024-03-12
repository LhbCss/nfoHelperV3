package BO;

import com.thoughtworks.xstream.annotations.;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.basic.StringConverter;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class movie implements Serializable {
    /**
     * 标题
     */
    @("title")
    private String title;
    /**
     * 评分
     */
    @("rating")
    private Double rating;
    /**
     * 简介
     */
    @("plot")
    private String plot;
    /**
     * runTime
     */
    @("runtime")
    private Integer runtime;
    /**
     * mpaa
     */
    @("mpaa")
    private String mpaa;
    /**
     * 标签 List
     */
    @("genre")
    private List<String> genre;
    /**
     * tag List
     */
    @("tag")
    private List<String> tag;
    /**
     * 国家
     */
    @("country")
    private String country;
    /**
     * 导演
     */
    @("director")
    private String director;
    /**
     * 发行时间
     */
    @("premiered")
    private Date premiered;
    /**
     * 工作室
     */
    @("studio")
    private String studio;
    /**
     * 地址 trailer
     */
    @("trailer")
    private String trailer;
    /**
     * UniqueId
     */
    @("uniqueid")
    private List<UniqueID> uniqueid;
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class UniqueID {
        @XStreamAsAttribute
        private String type;

        @XStreamAsAttribute
        private String defaultValue;

        @XStreamConverter(value = StringConverter.class)
        private String value;
    }
}
