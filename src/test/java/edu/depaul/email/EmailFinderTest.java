package edu.depaul.email;

import static edu.depaul.email.StorageService.StorageType.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jdk.nashorn.internal.runtime.regexp.joni.constants.Arguments;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;

import javax.jws.WebMethod;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Stream;

public class EmailFinderTest {

    @Test
    @Order(12)
    @DisplayName("EmailFinder - end to end test, checking if the whole program works as it should")
    void EmailFinderTest() {
        EmailFinder finder = new EmailFinder();
        String[] arg = {"C:\\Users\\rserna\\Documents\\email-finder-1\\src\\test\\resources\\emailsMany.html"};
        finder.run(arg);
        PageFetcher fetcher = new PageFetcher();
        Document doc = fetcher.get("C:\\Users\\rserna\\Documents\\email-finder-1\\email.txt");
        assertEquals("AvatarAang@AirNomads.net rickeyserna7@live.com lawrencetalbot@lupin.org CarlosSantana@SantanaBand.com songohan@orangestarhigh.edu pparker1@midtownhigh.edu jack@heresjohnny.com SaulHudson@gunsnroses.net ClarkKent@DailyPlanet.org BugsBunny@whatsupdoc.org VanHelsing@vampirehunter.org songoku@zfighters.org", doc.text());
    }

    @Test
    @Order(0)
    @DisplayName("ListWriter - standard test, does writeList add to OutputStream as it should")
    void listWriterTest() throws IOException {
        OutputStream stream = new ByteArrayOutputStream(1024);
        ListWriter writer = new ListWriter(stream);
        Collection<String> bList = Arrays.asList("Wally", "West", "Central", "City", "Hero");
        writer.writeList(bList);
        assertEquals("Wally\nWest\nCentral\nCity\nHero\n", stream.toString());
    }

    @Test
    @Order(1)
    @DisplayName("Crawler - return after max emails" +
            "bug found in the code, only 5 emails should be found as this was the maxEmails," +
            "but program kept going and recorded all 10 emails")
    void crawlerTest1() {
        StorageService storage = new StorageService();
        PageCrawler crawler = new PageCrawler(storage, 5);
        crawler.crawl("C:\\Users\\rserna\\Documents\\email-finder-1\\src\\test\\resources\\emailsMany.html");
        Set<String> emails = crawler.getEmails();
        assertEquals(5, emails.size());
    }

    @Test
    @Order(2)
    @DisplayName("Crawler - checking report() returns as expected")
    void crawlerTest2() {
        StorageService storage = new StorageService();
        storage.addLocation(EMAIL, "C:\\Users\\rserna\\Documents\\email-finder-1\\email.txt");
        storage.addLocation(GOODLINKS, "C:\\Users\\rserna\\Documents\\email-finder-1\\good-links.txt");
        storage.addLocation(BADLINKS, "C:\\Users\\rserna\\Documents\\email-finder-1\\badlinks.txt");
        PageCrawler crawler = new PageCrawler(storage, 50);
        crawler.crawl("C:\\Users\\rserna\\Documents\\email-finder-1\\src\\test\\resources\\emailsMany.html");
        crawler.report();
        PageFetcher fetcher = new PageFetcher();
        Document doc = fetcher.get("C:\\Users\\rserna\\Documents\\email-finder-1\\email.txt");
        assertEquals("AvatarAang@AirNomads.net rickeyserna7@live.com lawrencetalbot@lupin.org CarlosSantana@SantanaBand.com songohan@orangestarhigh.edu pparker1@midtownhigh.edu jack@heresjohnny.com SaulHudson@gunsnroses.net ClarkKent@DailyPlanet.org BugsBunny@whatsupdoc.org VanHelsing@vampirehunter.org songoku@zfighters.org", doc.text());
    }

    @Test
    @Order(3)
    @DisplayName("Crawler - seeing if crawl avoids endless loops" +
            "the site being crawled contains an email and a link to another site" +
            "the other site contains a different email and a link to the original site" +
            "if crawl does not avoid endless loops, they will go back and forth between each other endlessly" +
            "and the two emails will be written into the text file over and over")
    void crawlerTest3() {
        StorageService storage = new StorageService();
        storage.addLocation(EMAIL, "C:\\Users\\rserna\\Documents\\email-finder-1\\email.txt");
        storage.addLocation(GOODLINKS, "C:\\Users\\rserna\\Documents\\email-finder-1\\good-links.txt");
        storage.addLocation(BADLINKS, "C:\\Users\\rserna\\Documents\\email-finder-1\\badlinks.txt");
        PageCrawler crawler = new PageCrawler(storage, 50);
        crawler.crawl("C:\\Users\\rserna\\Documents\\email-finder-1\\src\\test\\resources\\linkedFile1.html");
        crawler.report();
        PageFetcher fetcher = new PageFetcher();
        Document doc = fetcher.get("C:\\Users\\rserna\\Documents\\email-finder-1\\email.txt");
        assertEquals("rickeyserna7@live.com SaulHudson@GunsNRoses.net", doc.text());
    }

    @Test
    @Order(4)
    @DisplayName("Fetcher - getString() test")
    void fetcherTest1() {
        String resultString = "<html> \n" +
                " <head> \n" +
                "  <title>CNN - O.J. Simpson Trial</title> \n" +
                " </head> \n" +
                " <body bgcolor=\"#FFFFFF\"> \n" +
                "  <center> <a href=\"/US/OJ/MAPS/OJ_main.map\"> <img alt=\"CNN O.J. Simpson Trial\" border=\"0\" height=\"66\" width=\"470\" src=\"/US/OJ/images/OJ_main.gif\" ismap></a> \n" +
                "  </center>\n" +
                "  <p></p> <!----> <a href=\"/US/9609/25/simpson/\"><img src=\"/US/9609/25/simpson/simpson.generic.jpg\" alt=\"Simpson\" align=\"RIGHT\" width=\"157\" height=\"133\" border=\"0\"></a> \n" +
                "  <h2>Simpson judge OKs jury prospects who admit bias</h2> \n" +
                "  <p> The pool of prospective jurors in the O.J. Simpson civil trial split along racial lines Tuesday, with whites saying Simpson was probably guilty of murder and African-Americans saying he is innocent. <br> <a href=\"/US/9609/25/simpson/\">-Full story-</a></p> \n" +
                "  <br clear=\"ALL\">\n" +
                "  <hr width=\"40%\"> <!----> \n" +
                "  <center> \n" +
                "   <p></p>\n" +
                "   <table cellpadding=\"1\"> \n" +
                "    <tbody>\n" +
                "     <tr align=\"center\" valign=\"top\"> \n" +
                "      <td><a href=\"verdict/index.html\">The Verdict</a></td> \n" +
                "      <td><a href=\"suspect/index.html\">The Suspect</a></td> \n" +
                "      <td><a href=\"victims/index.html\">The Victims</a></td> \n" +
                "     </tr> \n" +
                "     <tr> \n" +
                "      <td><a href=\"verdict/index.html\"> <img hspace=\"5\" vspace=\"5\" border=\"0\" src=\"images/verdict_icon.gif\" width=\"84\" height=\"74\"></a></td> \n" +
                "      <td><a href=\"suspect/index.html\"> <img hspace=\"5\" vspace=\"5\" border=\"0\" src=\"images/oj.gif\" width=\"84\" height=\"84\"></a></td> \n" +
                "      <td><a href=\"victims/index.html\"> <img hspace=\"5\" vspace=\"5\" border=\"0\" src=\"images/victims.gif\" width=\"84\" height=\"73\"></a></td> \n" +
                "     </tr> \n" +
                "    </tbody>\n" +
                "   </table>\n" +
                "   <p></p> \n" +
                "   <hr width=\"75%\"> \n" +
                "   <p></p>\n" +
                "   <table cellpadding=\"1\"> \n" +
                "    <tbody>\n" +
                "     <tr align=\"center\" valign=\"top\"> \n" +
                "      <td><a href=\"murder/index.html\">The Murder</a></td> \n" +
                "      <td><a href=\"arrest/index.html\">The Arrest</a></td> \n" +
                "      <td><a href=\"evidence/index.html\">The Evidence</a></td> \n" +
                "     </tr> \n" +
                "     <tr> \n" +
                "      <td><a href=\"murder/index.html\"> <img hspace=\"5\" vspace=\"5\" border=\"0\" src=\"images/murder.gif\" width=\"84\" height=\"68\"></a></td> \n" +
                "      <td><a href=\"arrest/index.html\"> <img hspace=\"5\" vspace=\"5\" border=\"0\" src=\"images/arrest.gif\" width=\"97\" height=\"75\"></a></td> \n" +
                "      <td><a href=\"evidence/index.html\"> <img hspace=\"5\" vspace=\"5\" border=\"0\" src=\"images/evidence.gif\" width=\"97\" height=\"84\"></a></td> \n" +
                "     </tr> \n" +
                "    </tbody>\n" +
                "   </table>\n" +
                "   <p></p> \n" +
                "   <hr width=\"75%\"> \n" +
                "   <p></p>\n" +
                "   <table cellpadding=\"1\"> \n" +
                "    <tbody>\n" +
                "     <tr align=\"center\" valign=\"top\"> \n" +
                "      <td><a href=\"players/index.html\">The Players</a></td> \n" +
                "      <td><a href=\"trial/index.html\">The Trial</a></td> \n" +
                "      <td><a href=\"otherviews.html\">Other Views</a></td> \n" +
                "     </tr> \n" +
                "     <tr> \n" +
                "      <td><a href=\"players/index.html\"> <img hspace=\"5\" vspace=\"5\" border=\"0\" src=\"images/players.gif\" width=\"76\" height=\"114\"></a></td> \n" +
                "      <td><a href=\"trial/index.html\"> <img hspace=\"5\" vspace=\"5\" border=\"0\" src=\"images/gavel.gif\" width=\"103\" height=\"56\"></a></td> \n" +
                "      <td><a href=\"otherviews.html\"> <img hspace=\"5\" vspace=\"5\" border=\"0\" src=\"images/views.gif\" width=\"103\" height=\"69\"></a></td> \n" +
                "     </tr> \n" +
                "    </tbody>\n" +
                "   </table>\n" +
                "   <p></p> \n" +
                "  </center> \n" +
                "  <br clear=\"all\"> \n" +
                "  <h4>More stories</h4> \n" +
                "  <ul> \n" +
                "   <li><a href=\"/US/9606/12/simpson.cast/index.html\">Two years later, Simpson story still being played out</a> - June 12 </li>\n" +
                "   <li><a href=\"/US/9605/29/oj.investigation/index.html\">PIs offer Simpson free sleuthing</a> - May 29 </li>\n" +
                "   <li><a href=\"/US/9605/26/simspon.wir/index.html\">Simpson depositions winding down</a> - May 26 </li>\n" +
                "   <li><a href=\"/US/9605/15/oj.oxford/index.html\">Simpson defends himself at Oxford</a> - May 15 </li>\n" +
                "   <li><a href=\"/US/9605/14/simpson.lien/index.html\">IRS slaps lien on O.J. Simpson's mansion</a> - May 14 </li>\n" +
                "   <li><a href=\"/US/9604/29/fuhrman.advancer/index.html\">Fuhrman mum in deposition for Simpson civil suit</a> - April 29 </li>\n" +
                "   <li><a href=\"/US/9604/02/simpson/index.html\">Nicole Simpson planned sexual encounter with Goldman, friend says</a> - April 2 </li>\n" +
                "   <li><a href=\"/US/9604/01/simpson_juror/index.html\">Former O.J. juror says she was victim of jury tampering</a> - April 1 </li>\n" +
                "   <li><a href=\"/US/OJ/daily/9603/30/index.html\">Officials probing possible jury tampering in Simpson trial</a> - March 30 </li>\n" +
                "   <li><a href=\"/US/OJ/daily/9603/26/index.html\">Furhman deposition delayed in Simpson case</a> - March 26 </li>\n" +
                "   <li><a href=\"/US/OJ/daily/9603/16/index.html\">Darden criticizes most players in Simpson case, but not Clark</a> - March 16 </li>\n" +
                "   <li><a href=\"/US/OJ/daily/9603/05/index.html\">Simpson recalls day of murders</a> - March 5 </li>\n" +
                "   <li><a href=\"/US/OJ/daily/9603/03/index.html\">Simpson denies events in ex-wife's diary</a> - March 3 </li>\n" +
                "   <li><a href=\"/US/OJ/daily/9603/01/index.html\">Simpson trial date moved to September</a> - March 1 </li>\n" +
                "   <li><a href=\"/US/OJ/daily/9602/28/simpson/index.html\">'Kato' says Nicole predicted her murder</a> - February 28 </li>\n" +
                "   <li><a href=\"/US/OJ/daily/9602/28/simpson_attorney/index.html\">Simpson's attorney angry over publicity</a> - February 28 </li>\n" +
                "   <li><a href=\"/US/OJ/daily/9602/27/index.html\">No settlement, Goldman family says</a> - February 27 </li>\n" +
                "   <li><a href=\"/US/OJ/daily/9602/06/he_said/index.html\">O.J.: He said...They said</a> - February 6 </li>\n" +
                "   <li><a href=\"/US/OJ/daily/9602/05/goldman.html\">Kim Goldman says she hates O.J.</a> - February 5 </li>\n" +
                "   <li><a href=\"/US/OJ/daily/9602/05/deposition/index.html\">Simpson case: Goldmans to be questioned</a> - February 5 </li>\n" +
                "   <li><a href=\"/US/OJ/daily/9602/05/oj_burden_proof/index.html\">Simpson makes spontaneous call to \"Burden of Proof\"</a> - February 5 </li>\n" +
                "   <li><a href=\"/US/OJ/daily/9602/05/oj_burden_proof/index.html\">O.J. talks to CNN: Simpson makes spontaneous call to \"Burden of Proof\"</a> - February 5 </li>\n" +
                "   <li><a href=\"/US/OJ/daily/9602/05/goldman.html\">Ron Goldman's sister questioned about her brother</a> - February 5 </li>\n" +
                "   <li><a href=\"/US/OJ/daily/9602/05/deposition/index.html\">Simpson case: Goldmans to be questioned</a> - February 5 </li>\n" +
                "   <li><a href=\"/US/OJ/daily/9602/03/index.html\">Simpson: Nicole invented abuse charges</a> - February 3 </li>\n" +
                "   <li><a href=\"/US/OJ/daily/9601/01-31/index.html\">Source: Simpson alibi conflicts with limo driver's testimony</a> - January 31 </li>\n" +
                "   <li><a href=\"/US/OJ/daily/9601/01-26/index.html\">Simpson deposition postponed for a week</a> - January 26 </li>\n" +
                "   <li><a href=\"daily/9601/01-25/interview/index.html\">Simpson: 'I couldn't kill anyone'</a> - January 25 </li>\n" +
                "   <li><a href=\"/US/OJ/daily/9601/01-24/index.html\">Simpson keeps low profile during day 3 of deposition</a> - January 24 </li>\n" +
                "   <li><a href=\"daily/9601/01-23/pm/index.html\">Goldman says hopes lifted by Simpson questioning</a> - January 23 </li>\n" +
                "   <li><a href=\"daily/9601/01-23/index.html\">Simpson begins deposition in wrongful death suits</a> - January 23 </li>\n" +
                "   <li><a href=\"/US/OJ/daily/9601/01-22/index.html\">Simpson arrives to begin deposition</a> - January 22 </li>\n" +
                "   <li><a href=\"/US/OJ/daily/9601/01-21/index.html\">O.J. Simpson to speak to the court in civil cases</a> - January 21 </li>\n" +
                "   <li><a href=\"/US/Newsbriefs/9601/01-16/index.html#3\">Judge clears hurdle to Simpson deposition</a> - January 16 </li>\n" +
                "   <li><a href=\"daily/9601/01-05/index.html\">O.J. Simpson deposition postponed</a> - January 5 </li>\n" +
                "  </ul> \n" +
                "  <hr> \n" +
                "  <h2>The Verdict</h2> \n" +
                "  <ul> \n" +
                "   <li>A multi-media view of the verdict: <a href=\"daily/9510/10-03/gallery/gallery1.html\"><img src=\"/icon/picture.icon.gif\" alt=\"|Images |\" align=\"absmiddle\" border=\"1\"></a> <a href=\"daily/9510/10-04/sounds/index.html\"><img src=\"/icon/sndicon.gif\" alt=\" Sounds |\" align=\"absmiddle\" border=\"1\"></a> <a href=\"daily/9510/10-04/movies/index.html\"><img src=\"/icon/movicon.gif\" alt=\" Movies |\" align=\"absmiddle\" border=\"1\"></a> </li>\n" +
                "   <li><a href=\"daily/9510/10-04/poll/index.html\"><img src=\"daily/9510/10-04/poll/poll_logo_icon.gif\" alt=\"poll logo\" align=\"absmiddle\" width=\"27\" height=\"25\" border=\"0\" hspace=\"3\">Simpson verdict opinion poll</a> </li>\n" +
                "  </ul> \n" +
                "  <ul> \n" +
                "   <li><a href=\"daily/9510/10-04/jurors_speak/index.html\">Jurors say evidence made the case for Simpson</a> - October 4 </li>\n" +
                "   <li><a href=\"verdict/reaction/index.html\">Sobbing, elation at Simpson verdict</a> - October 3 </li>\n" +
                "   <li><a href=\"verdict/prosecution/index.html\">The case for the prosecution</a> - October 3 </li>\n" +
                "   <li><a href=\"verdict/defense/index.html\">The case for the defense</a> - October 3 </li>\n" +
                "   <li><a href=\"verdict/index.html\">The verdict: how the defense prevailed</a> - October 3 </li>\n" +
                "   <li><a href=\"daily/9510/10-03/figures/index.html\">The numbers behind the case</a> - October 3 </li>\n" +
                "   <li><a href=\"daily/9510/10-03/index.html\">\"Trial of the century\" ends with Simpson's acquittal</a> - October 3 </li>\n" +
                "  </ul> \n" +
                "  <h4>The Reaction</h4> \n" +
                "  <ul> \n" +
                "   <li><a href=\"daily/9510/10-04/women_react/index.html\">Many women outraged at O.J. verdict</a> - October 4 </li>\n" +
                "   <li><a href=\"daily/9510/10-04/mcdermott/index.html\">Simpson camp rejoices while adversaries grieve</a> - October 4 </li>\n" +
                "   <li><a href=\"verdict/world/index.html\">Simpson trial draws jeers 'round the world</a> </li>\n" +
                "   <li><a href=\"verdict/political/index.html\">Politicians speak out on Simpson verdict</a> - October 3 </li>\n" +
                "   <li><a href=\"daily/9510/10-03/home/index.html\">Champagne and hugs greet Simpson at home</a> - October 3 </li>\n" +
                "  </ul> \n" +
                "  <h4>What's Next</h4> \n" +
                "  <ul> \n" +
                "   <li><a href=\"daily/9510/10-04/kids/index.html\">Simpson may get custody of the children</a> - October 4 </li>\n" +
                "   <li><a href=\"daily/9510/10-04/whats_next/index.html\">What's next for O.J.?</a> - October 4 </li>\n" +
                "   <li><a href=\"/SHOWBIZ/misc/9510/image_handlers/index.html\">Simpson trial's bit players become deal makers</a> - October 3 </li>\n" +
                "   <li><a href=\"daily/9510/10-03/fuhrman/index.html\">Justice Department looks into Fuhrman tapes</a> - October 3 </li>\n" +
                "  </ul> \n" +
                "  <br clear=\"ALL\"> \n" +
                "  <hr width=\"40%\"> \n" +
                "  <br clear=\"ALL\"> \n" +
                "  <p><b><a href=\"more.html\">More Simpson stories</a></b></p> <!----> \n" +
                "  <img src=\"/US/OJ/images/whatyouthink.gif\" width=\"62\" height=\"62\" alt=\"What you think\" align=\"LEFT\"> \n" +
                "  <h3>What do you think?</h3> <b>View <a href=\"/US/OJ/feedback/feedback.html\">comments from other users on the trial verdict.</a></b> \n" +
                "  <br clear=\"ALL\">\n" +
                "  <hr width=\"40%\"> <!----> \n" +
                "  <br clear=\"all\"> \n" +
                "  <hr size=\"3\"> \n" +
                "  <center> <a href=\"/US/MAPS/us_nav.map\"> <img alt=\"[Imagemap]\" border=\"0\" width=\"470\" height=\"18\" src=\"/US/OJ/images/main_nav.gif\" ismap></a> \n" +
                "   <h5>| <a href=\"/INDEX/index.html\">CONTENTS</a> | <a href=\"/SEARCH/index.html\">SEARCH</a> | <a href=\"/index.html\">CNN HOME PAGE</a> | <a href=\"/US/index.html\">MAIN U.S. NEWS PAGE</a> |</h5> \n" +
                "   <hr size=\"3\"> \n" +
                "   <p> <em>Copyright © 1995 Cable News Network, Inc. ALL RIGHTS RESERVED.</em> </p> \n" +
                "  </center>  \n" +
                " </body>\n" +
                "</html>";
        PageFetcher fetcher = new PageFetcher();
        assertEquals(resultString, fetcher.getString("http://www.cnn.com/US/OJ/"));
    }

    @Test
    @Order(5)
    @DisplayName("Fetcher - get() test")
    void fetcherTest2Parameterized() {
        PageFetcher fetcher = new PageFetcher();
        Document b = fetcher.get("http://www.cnn.com/US/OJ/");
        assertEquals("CNN - O.J. Simpson Trial", b.title());
    }

    @Test
    @Order(6)
    @DisplayName("Fetcher - get(), bad URL tests")
    void fetcherTest3AssertAll() {
        PageFetcher fetcher = new PageFetcher();
        assertAll("several bad urls",
                () -> {
                    assertThrows(EmailFinderException.class, () -> fetcher.get("thisIsNotAUrlIsIt?"));
                },
                () -> {
                    assertThrows(EmailFinderException.class, () -> fetcher.get("even worse url!"));
                },
                () -> {
                    assertThrows(EmailFinderException.class, () -> fetcher.get("!@#$%&*()^$#?"));
                });
    }

    @Test
    @Order(7)
    @DisplayName("Fetcher - get(), seemingly valid URL, but IOException because the website does not exist test")
    void fetcherTest4() {
        PageFetcher fetcher = new PageFetcher();
        assertThrows(EmailFinderException.class, () -> fetcher.get("http://www.iuohr98he98hspacejam.com/"));
    }

    @Test
    @Order(8)
    @DisplayName("PageParser - check if findEmails() is returning expected set with valid HTML file")
    void parserTest1(){
        PageFetcher fetcher = new PageFetcher();
        PageParser parser = new PageParser();
        Document doc = fetcher.get("C:\\Users\\rserna\\Documents\\email-finder-1\\src\\test\\resources\\emails.html");
        Set<String> result = parser.findEmails(doc);
        Set<String> emails = new HashSet<>(Arrays.asList("rickeyserna7@live.com", "SaulHudson@gunsnroses.net"));
        assertEquals(emails, result);
    }

    @Test
    @Order(9)
    @DisplayName("PageParser - check if findLinks() is returning expected set with valid HTML file")
    void parserTest2(){
        PageFetcher fetcher = new PageFetcher();
        PageParser parser = new PageParser();
        Document doc = fetcher.get("C:\\Users\\rserna\\Documents\\email-finder-1\\src\\test\\resources\\links.html");
        Set<String> result = parser.findLinks(doc);
        Set<String> emails = new HashSet<>(Arrays.asList("https://www.wikipedia.org/", "https://www.google.com/"));
        assertEquals(emails, result);
    }

    @Test
    @Order(10)
    @DisplayName("PageParser - running findLinks() on a page with no a tags, should not find anything")
    void parserTest3(){
        PageFetcher fetcher = new PageFetcher();
        PageParser parser = new PageParser();
        Document doc = fetcher.get("C:\\Users\\rserna\\Documents\\email-finder-1\\src\\test\\resources\\linksNoATags.html");
        Set<String> result = parser.findLinks(doc);
        Set<String> emails = new HashSet<>();
        assertEquals(emails, result);
    }

    @Test
    @Order(11)
    @DisplayName("StorageService - standard test, does storeList write to the file as specified?")
    void storageServiceTest(){
        StorageService storage = new StorageService();
        storage.addLocation(EMAIL, "C:\\Users\\rserna\\Documents\\email-finder-1\\email.txt");
        Collection<String> bList = Arrays.asList("Bruce", "Wayne", "Billionaire", "Playboy", "Philanthropist");
        storage.storeList(EMAIL, bList);
        PageFetcher fetcher = new PageFetcher();
        Document doc = fetcher.get("C:\\Users\\rserna\\Documents\\email-finder-1\\email.txt");
        assertEquals("Bruce Wayne Billionaire Playboy Philanthropist", doc.text());
    }

}
