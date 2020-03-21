package cz.muni.fi.gag.web.services.mapped;

import cz.muni.fi.gag.web.services.service.generic.GenericCRUDService;
import cz.muni.fi.gag.web.persistence.entity.GenericEntity;

/**
 * @author Vojtech Prusa
 * this is only sketch ....
 */
interface EntityMapper<OF extends GenericEntity, FROM extends Object, TO extends GenericEntity,
        USING extends GenericCRUDService> {

    FROM getFrom();
    void setFrom(FROM from);
    //void setFromTo(USING using);
    OF to(USING using);
}