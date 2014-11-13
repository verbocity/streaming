package com.ing.streaming.actors

import akka.actor.Actor
import com.ing.streaming.actors.traits.StreamAction
import com.ing.streaming.data.Event
import com.ing.streaming.spark.HttpSend
import org.apache.spark.rdd.RDD
import org.apache.spark.SparkContext._

import scala.collection.immutable.Iterable

class WordCloudActor extends Actor with HttpSend {
  override def receive = {
    case StreamAction(rdd: RDD[Event]) =>
      if (rdd.count() > 0) {
        val shopList: Iterable[String] = shopMap.flatMap(x => x._2)

        // Only select the events which contain known shops
        val containsShops = rdd.filter(e =>
          shopList.exists(shop => e.getField("description").contains(shop))
        ).collect()

        // Convert the list of descriptions that contain shops to the actual shop names they contain
        val shopNames = containsShops.map(e => {
          toShopName(e.getField("description"), shopList)
        })

        // Group by and get counts for top 10 of shops
        val topShops: List[(String, String, Int)] = shopNames.groupBy(c => c)
          .mapValues(_.size)
          .toList
          .sortBy(_._2)
          .takeRight(10)
          .map(a => (a._1, getURL(a._1), a._2))

        val json = toJson(topShops)
        send("shops", json)
      }

      def toShopName(descr: String, shopList: Iterable[String]): String = {
        for (shop <- shopList) {
          if (descr.contains(shop)) {
            return shop
          }
        }

        descr
      }

      def getURL(shop: String): String = {
        val map = Map(
          "shell" -> "http://www.oalgroup.com/wp-content/uploads/shell_logo.jpg",
          "aldi" -> "http://www.aldi.nl/images/aldi-logo-big.png",
          "tinq" -> "https://www.tinq.nl/website/static/source/images/logo_tinq.gif",
          "esso" -> "http://upload.wikimedia.org/wikipedia/commons/9/9f/Logo_Esso.gif",
          "tango" -> "http://www.gvr-slagzinnenregister.nl/wp-content/uploads/2014/02/Tango.jpg",
          "total" -> "http://www.ltg.uk.com/cms/site/images/total-logo-528400043.jpg",
          "bp" -> "http://www.thermalimaging.co.uk/wp-content/uploads/BP-Use-Thermal-Imaging-To-Boost-Reliability-Oil-Gas.jpg",
          "mcdonald" -> "https://pbs.twimg.com/profile_images/453545705622106112/6ERHbJBN.jpeg",
          "texaco" -> "http://www.ukhaulier.co.uk/fuel-cards/images/texaco_showcase_logo.jpg",
          "avia" -> "http://asklogo.com/images/A/avia%20logo.jpg",
          "bijenkorf" -> "http://www.studentenwerk.nl/public_images/Image/youngcapital/bijenkorf.jpg",
          "spar" -> "http://www.slimpie.com/images/news/nieuw!-waterijs-bij-spar.jpg",
          "ill" -> "http://brainofbmw.com/wp-content/uploads/2012/10/ixi-300x231.jpg"
        )

        map.getOrElse(shop, "")
      }

      def toJson(list: List[(String, String, Int)]): String = {
        val result = {
          for {
            elem <- list
          } yield "{ \"name\": \"" + elem._1 + "\", \"url\": \"" + elem._2 + "\", \"count\": \"" + elem._3 + "\"}, "
        }.mkString(" ")

        "[" + result.substring(0, result.length() - 2) + "]"
      }
  }

  val shopMap: Map[String, List[String]] = Map(
  "autogarages" -> List(
    "euromaster",
    "kwikfit",
  "profile tyrecenter",
  "winterbanden"
  ),
  "baby winkels" -> List(
    "babydump",
    "prenatal"
  ),
  "bakkerijen en broodjeszaken" -> List(
    "bakkerij bart"
  ),
  "banken" -> List(
    "abn amro",
  "ing bank",
  "rabobank"
  ),
  "banketbakkerijen" -> List(
    "multivlaai"
  ),
  "bedden en matrassen" -> List(
    "beter bed",
    "energy plus",
  "mediactive",
  "morgana",
  "totalfit"
  ),
  "boekhandels en boekenwinkels" -> List(
    "ako",
    "boekenvoordeel",
    "bruna",
    "primera",
    "selexyz"
  ),
  "bouwmarkten (doe het zelf)" -> List(
    "gamma",
    "hubo",
    "karwei",
    "kwantum",
    "praxis"
  ),
  "cadeauartikelen" -> List(
    "waar",
    "woonwinkel nijade"
  ),
  "cd, dvd, blue-ray winkels" -> List(
    "free record shop",
    "van leest music & movies"
  ),
  "computer games" -> List(
    "game mania",
    "gamestore e plaza"
  ),
  "computerwinkels" -> List(
    "computerland",
    "dixons",
    "mycom"
  ),
  "dierenwinkels" -> List(
    "pets place"
  ),
  "doe het zelf en bouwmaterialen" -> List(
    "de vakman verf & wand"
  ),
  "drogisterijen" -> List(
    "da drogisterij",
    "dirx drogisterij",
    "erica",
    "etos",
    "kruidvat",
    "trekpleister"
  ),
  "elektronica winkels" -> List(
    "bcc",
    "mediamarkt",
    "scheer & foppen"
  ),
  "fastfood restaurants" -> List(
    "burger king",
    "dominos",
    "febo",
    "mcdonald",
    "new york pizza"
  ),
  "fiets-, scooter- en brommerwinkels" -> List(
    "willemstraatbike"
  ),
  "hobbywinkels" -> List(
    "pipoos"
  ),
  "horecazaken" -> List(
    "kaldi"
  ),
  "huishoud winkels" -> List(
    "action",
    "blokker",
    "euroland",
    "handyman"
  ),
  "hypotheken en hypotheekadvies" -> List(
    "hypotheker",
    "hypotheek visie"
  ),
  "kampeerartikelen en outdoorartikelen" -> List(
    "bever"
  ),
  "keukens" -> List(
    "grando keukens"
  ),
  "keukens en badkamers" -> List(
    "brugman keukens",
    "bruynzeel keukens",
    "keukenconcurrent"
  ),
  "kledingwinkels (dames en heren)" -> List(
    "america today",
    "cool cat",
    "esprit store",
    "forecast",
    "hm",
    "houtbrox",
    "jeans centre",
    "manfield",
    "men at work",
    "mexx",
    "new tailor",
    "newyorker",
    "setpoint",
    "shopaway",
    "the sting"
  ),
  "kledingwinkels (dames)" -> List(
    "didi",
    "la ligna",
    "le ballon",
    "miss etam",
    "ms mode",
    "only",
    "promiss",
    "sissy boy",
    "steps",
    "superstar",
    "terstal",
    "vero moda",
    "wg designs",
    "we women"
  ),
  "kledingwinkels (heren)" -> List(
    "adam menswear",
    "chasin",
    "ill",
    "jack jones",
    "score",
    "suitable kleding",
    "we men"
  ),
  "kledingwinkels (kinderen)" -> List(
    "villa happ",
    "wibra",
    "zeeman"
  ),
  "lingerie winkels" -> List(
    "christine le",
    "hunkemoller",
    "livera"
  ),
  "opticiens en brillenzaken" -> List(
    "wish groeneveld",
    "hans anders",
    "huis opticiens",
    "pearle",
    "specsavers"
  ),
  "parfumerie winkels" -> List(
    "douglas",
    "ici paris",
    "rituals",
    "body shop"
  ),
  "parket en liminaat vloeren" -> List(
    "roobol woonwinkels"
  ),
  "postkantoren" -> List(
    "postnl"
  ),
  "schoenen winkels" -> List(
    "bristol",
    "cinderella",
    "dolcis",
    "invito",
    "sacha shoes",
    "scapino",
    "shoeline",
    "van dalen",
    "van haren"
  ),
  "slijterijen" -> List(
    "gall gall",
    "mitra"
  ),
  "snoepzaken" -> List(
    "jamin"
  ),
  "speelgoed winkels" -> List(
    "bart smit",
    "bart speelgoedwinkel",
    "intertoys"
  ),
  "sport winkels" -> List(
    "aktiesport",
    "foot locker",
    "intersport",
    "perry sport",
    "pro sport",
    "run2day",
    "runnersworld"
  ),
  "tegelhandels" -> List(
    "wincie natuursteen"
  ),
  "telefoon winkels" -> List(
    "belcompany",
    "kpn winkel",
    "phone house"
  ),
  "tuincentra" -> List(
    "intratuin",
    "tuincentrum groenrijk",
    "tuincentrum overvecht"
  ),
  "videotheken" -> List(
    "filmclub",
    "movie max",
    "ster videotheek",
    "videoland"
  ),
  "vvv kantoren" -> List(
    "vvv"
  ),
  "warenhuizen" -> List(
    "badkamerwarenhuis utrecht",
    "bijenkorf",
    "ca",
    "hema",
    "kijkshop",
    "vd",
    "xenos"
  ),
  "meubelzaken" -> List(
    "designsalesnl",
    "ikea",
    "leen bakker",
    "montel",
    "profijt meubel",
    "rofra home"
  ),
  "mode accessoires" -> List(
    "lucardi juwelier",
    "six",
    "natuurwinkels",
    "natuurwinkel"
  ),
  "opticiens en brillenzaken" -> List(
    "wish groeneveld",
    "hans anders",
    "huis opticiens",
    "pearle",
    "specsavers"
  ),
  "parfumerie winkels" -> List(
    "douglas",
    "ici paris",
    "rituals",
    "body shop"
  ),
  "parket en liminaat vloeren" -> List(
    "roobol woonwinkels"
  ),
  "postkantoren" -> List(
    "postnl"
  ),
  "schoenen winkels" -> List(
    "bristol",
    "cinderella",
    "dolcis",
    "invito",
    "sacha shoes",
    "scapino",
    "shoeline",
    "van dalen",
    "van haren"
  ),
  "slijterijen" -> List(
    "gall gall",
    "mitra"
  ),
  "snoepzaken" -> List(
    "jamin"
  ),
  "speelgoed winkels" -> List(
    "bart smit",
    "bart speelgoedwinkel",
    "intertoys"
  ),
  "sport winkels" -> List(
    "aktiesport",
    "foot locker",
    "intersport",
    "perry sport",
    "pro sport",
    "run2day",
    "runnersworld"
  ),
  "supermarkten" -> List(
    "agrimarkt",
    "albert heijn",
    "aldi",
    "attent",
    "boni",
    "c1000",
    "coop",
    "deen",
    "dekamarkt",
    "dirk",
    "emte",
    "hoogvliet",
    "linders",
    "jumbo",
    "lidl",
    "mcd",
    "nettorama",
    "plus",
    "poiesz",
    "spar",
    "supercoop",
    "troefmarkt",
    "vomar"
  ),
  "tankstations en benzinestations" -> List(
    "amigo",
    "argos",
    "avia",
    "bp",
    "brandoil",
    "elan",
    "esso",
    "gulf",
    "q8",
    "shell",
    "supertank",
    "tamoil",
    "tango",
    "texaco",
    "tinq",
    "total"
  ),
  "tegelhandels" -> List(
    "wincie natuursteen"
  ),
  "telefoon winkels" -> List(
    "belcompany",
    "kpn winkel",
    "phone house"
  ),
  "tuincentra" -> List(
    "intratuin",
    "tuincentrum groenrijk",
    "tuincentrum overvecht"
  ),
  "videotheken" -> List(
    "filmclub",
    "movie max",
    "ster videotheek",
    "videoland"
  ),
  "vvv kantoren" -> List(
    "vvv"
  ),
  "warenhuizen" -> List(
    "badkamerwarenhuis utrecht",
    "bijenkorf",
    "ca",
    "hema",
    "kijkshop",
    "vd",
    "xenos"
  )
  )
}