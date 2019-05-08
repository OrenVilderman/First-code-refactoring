import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class GmailSendAndDelete {
	private static String userName;
	private static String userPass;
	private static String searchKey;
	private static int loopCounter;

	public static void main(String[] args) throws Exception {
		
		infoPrint("Start program - Gmail Send And Delete.");
	
		userName = getUserDetails("userName");
		userPass = getUserDetails("userPass");
		searchKey = getUserDetails("searchKey");
		// Set here the number of loops you want the program to try to perform
		// (Compose will run for(loopCounter*50) times)
		loopCounter = 4;

		// Call the loop that compose new email
		WebDriver chrome = getDriver();
		logInCompose(chrome);
		driverQuit(chrome, "Done composing.\n",
				"\nSomething was thrown when trying to \"quit\" the \"quit\" driver, error:\n\n");

		// Call the loop that delete emails with the keyword
		chrome = getDriver();
		logInSearchDelete(chrome);
		driverQuit(chrome, "Done deleting.\n",
				"\nSomething was thrown when trying to \"quit\" the \"quit\" driver, error:\n\n");

		driverClose(chrome, "Driver closed.\n",
				"\nDriver could not be closed, error:\n");
		
		infoPrint("End program - Gmail Send And Delete.");

	}

	// Methods
	private static void logInCompose(WebDriver driver) throws InterruptedException {
		driver.get("https://mail.google.com/mail/u/0/");
		// LogIn to Gmail
		gmailLogIn(driver, userName, userPass);
		// Inside the Inbox
		composeMailsLoop(driver, loopCounter);
		driver.quit();
	}

	private static void logInSearchDelete(WebDriver driver) {
		driver.get("https://mail.google.com/mail/u/0/");
		// LogIn to Gmail
		gmailLogIn(driver, userName, userPass);
		// Inside the Inbox
		WebElement searchBox = driver.findElement(By.cssSelector("input[name='q']"));
		searchBox.sendKeys(searchKey);
		searchBox.sendKeys(Keys.ENTER);
		performTrashLoop(driver, loopCounter);
		driver.quit();
	}

	private static void gmailLogIn(WebDriver driver, String userName, String userPass) {
		WebElement elementEmail = driver.findElement(By.cssSelector("input[type='email']"));
		elementEmail.sendKeys(userName + Keys.ENTER);
		WebDriverWait waitClickable = new WebDriverWait(driver, 10);
		waitClickable.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[type='password']")));
		WebElement elementPass = driver.findElement(By.cssSelector("input[type='password']"));
		elementPass.sendKeys(userPass + Keys.ENTER);
	}

	private static void composeMailsLoop(WebDriver driver, int loopCounter) throws InterruptedException {
		for (int i = 0; i < (loopCounter * 50); i++) {
			System.out.println("Starting compose loop " + (i + 1) + "/" + (loopCounter * 50));
			try {
				driver.manage().timeouts().implicitlyWait(600, TimeUnit.MILLISECONDS);
				WebElement compose = driver.findElement(By.cssSelector(".nH.bkL .z0 div"));
				compose.click();

				WebDriverWait waitVisible = new WebDriverWait(driver, 10);
				waitVisible.until(
						ExpectedConditions.textToBePresentInElementLocated(By.cssSelector(".aYF"), "New Message"));

				WebElement emailTo = driver.findElement(By.cssSelector("div [aria-label='New Message'] .bze"));
				WebElement emailSubject = driver.findElement(By.cssSelector("div [aria-label='New Message'] .aoT"));

				Actions actions = new Actions(driver);
				actions.moveToElement(emailTo);
				actions.click();
				actions.sendKeys(userName + "@gmail.com" + Keys.ENTER);
				actions.build().perform();

				emailSubject.sendKeys(searchKey + Keys.ENTER);

				WebElement emailSend = driver
						.findElement(By.cssSelector("div [aria-label='New Message'] .btC div [role='button']"));

				actions.moveToElement(emailSend);
				actions.click();
				actions.build().perform();
			} catch (Exception e) {
				System.out.println("Last email wasn't sent - Break!");
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				break;
			}
		}
	}

	private static void performTrashLoop(WebDriver driver, int loopCounter) {
		for (int i = 0; i < loopCounter; i++) {
			System.out.println("Starting delete loop " + (i + 1) + "/" + loopCounter);
			List<WebElement> emailsList = driver.findElements(
					By.cssSelector(".ae4.UI table[cellpadding='0'] tr[jsaction='bjyjJe:NOSeAe;pInidd:NOSeAe;']"));
			if (emailsList.size() == 0) {
				System.out.println("Stopped: Nothing to delete");
				break;
			}
			List<WebElement> containerList = driver.findElements(By.cssSelector(".D.E.G-atb"));
			var markAllContainer = containerList.get(containerList.size() - 1);
			var markAll = markAllContainer.findElement(By.cssSelector(".bzn div div div div"));
			Actions actions = new Actions(driver);
			actions.moveToElement(markAll);
			actions.click();
			actions.build().perform();
			var trash = markAllContainer.findElement(By.cssSelector("div[aria-label='Delete']"));
			trash.click();
		}
	}

	private static void driverQuit(WebDriver driver, String ifQuit, String ifException) {
		try {
			driver.quit();
			System.out.println(ifQuit);
		} catch (Exception e) {
			System.out.println(ifException + e + "\n");
		}
	}

	private static void driverClose(WebDriver driver, String ifClose, String ifException) {
		try {
			driver.close();
			System.out.println(ifClose);
		} catch (Exception e) {
			System.out.println(ifException + e + "\n");
		}
	}

	private static WebDriver getDriver() {
		Logger.getLogger("org.openqa.selenium.remote").setLevel(Level.OFF);
		System.setProperty("webchrome.chrome.silentOutput", "true");
		ChromeOptions options = new ChromeOptions();
		options.addArguments("disable-infobars");
		options.addArguments("incognito");
		options.addArguments("window-size=1050,1045");
		options.addArguments("window-position=877,0");
		WebDriver driver = new ChromeDriver(options);
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		return driver;
	}

	private static String getUserDetails(String What) {
		if (String.valueOf(What).equals("userName")) {
			return "Here is the place for your gmail account name befor the '@gmail.com part'"; // "" //"Here is the place for your gmail account name befor the '@gmail.com part'"
		} else if (String.valueOf(What).equals("userPass")) {
			return "Here is the place for your gmail password"; // "" //"Here is the place for your gmail password"
		} else if (String.valueOf(What).equals("searchKey")) {
			return "Temp.Email.Test"; // "";
		}
		return null;
	}
	
	private static void infoPrint(String string) {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = new Date();
		System.out.println(string + " (" + dateFormat.format(date) + ")\n");
	}
}