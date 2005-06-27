package games.stendhal.server.maps;

import marauroa.common.game.*;
import games.stendhal.server.*;
import games.stendhal.server.entity.*;

public class afterlive 
  {
  public afterlive(StendhalRPZone zone)
    {
    Portal portal=new Portal();
    zone.assignRPObjectID(portal);
    portal.setx(13);
    portal.sety(1);
    portal.setNumber(0);
    portal.setDestination("city",0);
    zone.add(portal);
    zone.addPortal(portal);

    portal=new Portal();
    zone.assignRPObjectID(portal);
    portal.setx(14);
    portal.sety(1);
    portal.setNumber(0);
    portal.setDestination("city",0);
    zone.add(portal);
    zone.addPortal(portal);
    
    Sign sign=new Sign();
    zone.assignRPObjectID(sign);
    sign.setx(11);
    sign.sety(19);
    sign.setText("I regret to tell you that you have died!|You have lost some of your items and 10% of your eXPerience points.|Be more careful next time. On the up side you can now return to city.");    
    zone.add(sign);
    }  
  }
