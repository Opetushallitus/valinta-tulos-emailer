package fi.vm.sade.vt.emailer

import fi.vm.sade.vt.emailer.valintatulos.LahetysSyy._
import fi.vm.sade.vt.emailer.valintatulos.{Hakukohde, Ilmoitus}
import org.specs2.mutable.Specification

class MailerHelperTest extends Specification {

  val helper: MailerHelper = new MailerHelper

  private val FI = "fi"

  "MailerHelper" should {
    "survive corner cases" in {
      helper.splitAndGroupIlmoitus(List.empty).size shouldEqual 0
      helper.splitAndGroupIlmoitus(List(getDummyIlmoitus(List()))) must throwA(new IllegalArgumentException("Empty hakukohdelist in hakemus null"))
    }

    "not split one simple ilmoitus" in {
      val ilmoitus = getDummyIlmoitus(List(vastaanottoilmoitusKk, vastaanottoilmoitusKk))

      val result = helper.splitAndGroupIlmoitus(List(ilmoitus))

      result.size shouldEqual 1
      result((FI, vastaanottoilmoitusKk)).size shouldEqual 1
      result((FI, vastaanottoilmoitusKk)).head.hakukohteet.size shouldEqual 2
    }

    "splits one simple ilmoitus" in {
      val ilmoitus = getDummyIlmoitus(List(vastaanottoilmoitusKk, sitovan_vastaanoton_ilmoitus))

      val result = helper.splitAndGroupIlmoitus(List(ilmoitus))

      result.size shouldEqual 2
      result((FI, sitovan_vastaanoton_ilmoitus)).size shouldEqual 1
      result((FI, sitovan_vastaanoton_ilmoitus)).head.hakukohteet.size shouldEqual 1
      result((FI, vastaanottoilmoitusKk)).size shouldEqual 1
      result((FI, vastaanottoilmoitusKk)).head.hakukohteet.size shouldEqual 1
    }

    "group multiple ilmoituses" in {
      val ilmoitus = getDummyIlmoitus(List(vastaanottoilmoitusKk, sitovan_vastaanoton_ilmoitus))
      val ilmoitus2 = getDummyIlmoitus(List(vastaanottoilmoitusKk, sitovan_vastaanoton_ilmoitus))
      val ilmoitus3 = getDummyIlmoitus(List(ehdollisen_periytymisen_ilmoitus))

      val result = helper.splitAndGroupIlmoitus(List(ilmoitus, ilmoitus2, ilmoitus3))

      result.size shouldEqual 3
      result((FI, sitovan_vastaanoton_ilmoitus)).size shouldEqual 2
      result((FI, sitovan_vastaanoton_ilmoitus)).head.hakukohteet.size shouldEqual 1
      result((FI, vastaanottoilmoitusKk)).size shouldEqual 2
      result((FI, vastaanottoilmoitusKk)).head.hakukohteet.size shouldEqual 1
      result((FI, ehdollisen_periytymisen_ilmoitus)).size shouldEqual 1
      result((FI, ehdollisen_periytymisen_ilmoitus)).head.hakukohteet.size shouldEqual 1
    }

  }

  def getDummyIlmoitus(hakukohteidenLahetysSyyt: List[LahetysSyy]): Ilmoitus = {
    Ilmoitus(null, null, None, FI, null, null, null,
      hakukohteidenLahetysSyyt.map(Hakukohde(null, true, null, null, _)),
      null)
  }

}
