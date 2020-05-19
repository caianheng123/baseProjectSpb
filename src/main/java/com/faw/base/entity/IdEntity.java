package com.faw.base.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import com.faw.utils.idgen.IdGenerate;
import com.faw.utils.lang.StringUtils;


import java.io.Serializable;
import java.util.Objects;



/**
 * IdEntity，建议所有entity继承
 *
 * @author liushengbin
 * @email liushengbin7@gmail.com
 * @date 2018-10-22 12:42:38
 */
public class IdEntity implements Serializable {

    @TableId(type = IdType.INPUT)
    protected String id;

    public String getId() {
        if (StringUtils.isBlank(id)) {
            id = IdGenerate.nextId();
        }
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "IdEntity{" +
                "id='" + id + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IdEntity)) {
            return false;
        }
        IdEntity idEntity = (IdEntity) o;
        return Objects.equals(id, idEntity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
