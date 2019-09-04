package cz.muni.fi.gag.web.mapped;

import cz.muni.fi.gag.web.entity.AbstractEntity;
import cz.muni.fi.gag.web.service.generic.GenericCRUDService;

/**
 * @author Vojtech Prusa
 * this is only sketch ....
 */
interface EntityMapper<OF extends AbstractEntity, FROM extends Object, TO extends AbstractEntity,
        USING extends GenericCRUDService> {

    FROM getFrom();
    void setFrom(FROM from);
    //void setFromTo(USING using);
    OF to(USING using);
}