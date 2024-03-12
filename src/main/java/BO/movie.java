package BO;

import com.thoughtworks.xstream.annotations.XStreamAlias;
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
    @XStreamAlias("title")
    private String title;
    /**
     * 评分
     */
    @XStreamAlias("rating")
    private Double rating;
    /**
     * 简介
     */
    @XStreamAlias("plot")
    private String plot;
    /**
     * runTime
     */
    @XStreamAlias("runtime")
    private Integer runtime;
    /**
     * mpaa
     */
    @XStreamAlias("mpaa")
    private String mpaa;
    /**
     * 标签 List
     */
    @XStreamAlias("genre")
    private List<String> genre;
    /**
     * tag List
     */
    @XStreamAlias("tag")
    private List<String> tag;
    /**
     * 国家
     */
    @XStreamAlias("country")
    private String country;
    /**
     * 导演
     */
    @XStreamAlias("director")
    private String director;
    /**
     * 发行时间
     */
    @XStreamAlias("premiered")
    private Date premiered;
    /**
     * 工作室
     */
    @XStreamAlias("studio")
    private String studio;
    /**
     * 地址 trailer
     */
    @XStreamAlias("trailer")
    private String trailer;
    /**
     * UniqueId
     */
    @XStreamAlias("uniqueid")
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
