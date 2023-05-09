// Model class for the URL entity
@Entity
public class Url {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String originalUrl;
    private String shortUrl;
    // getters and setters
}

// Repository interface for the URL entity
public interface UrlRepository extends CrudRepository<Url, Long> {
    Optional<Url> findByShortUrl(String shortUrl);
}

// Service class for the URL shortener
@Service
public class UrlService {
    @Autowired
    private UrlRepository urlRepository;
    private static final String BASE_URL = "http://localhost:8080/";

    public String shortenUrl(String originalUrl) {
        Optional<Url> existingUrl = urlRepository.findByOriginalUrl(originalUrl);
        if (existingUrl.isPresent()) {
            return BASE_URL + existingUrl.get().getShortUrl();
        } else {
            Url url = new Url();
            url.setOriginalUrl(originalUrl);
            url.setShortUrl(generateShortUrl());
            urlRepository.save(url);
            return BASE_URL + url.getShortUrl();
        }
    }

    private String generateShortUrl() {
        // generate a unique short URL using a hashing algorithm or other method
    }
}

// Controller class for the URL shortener
@Controller
public class UrlController {
    @Autowired
    private UrlService urlService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/shorten")
    public String shorten(@RequestParam("url") String originalUrl, Model model) {
        String shortUrl = urlService.shortenUrl(originalUrl);
        model.addAttribute("shortUrl", shortUrl);
        return "result";
    }

    @GetMapping("/{shortUrl}")
    public String redirect(@PathVariable("shortUrl") String shortUrl) {
        Optional<Url> url = urlRepository.findByShortUrl(shortUrl);
        if (url.isPresent()) {
            return "redirect:" + url.get().getOriginalUrl();
        } else {
            return "error";
        }
    }
}

<!-- View template for the URL shortener -->
<!DOCTYPE html>
<html>
<head>
    <title>URL Shortener</title>
</head>
<body>
    <h1>URL Shortener</h1>
    <form method="post" action="/shorten">
        <label for="url">Enter a URL to shorten:</label>
        <input type="text" name="url" id="url">
        <button type="submit">Shorten</button>
    </form>
    <p>Your shortened URL is: <a th:href="${shortUrl}"></a></p>
</body>
</html>