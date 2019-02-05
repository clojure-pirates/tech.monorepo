1. Export keys from gnupg

$ gpg --export -a test@test.com > test@test.com.public
$ gpg --export-secret-key -a test@test.com > test@test.com


$ cat test@test.com.public
-----BEGIN PGP PUBLIC KEY BLOCK-----

mQENBFvDT18BCADNGMwfI4UaqbaDR86oU6hTQI4xCcrPa4uPQydWjzsokgjgZCmc
9Ba81wlP8olNf71f4MEd68wo+zyAos5+E5Lc4/Wqcwfxwf6PDz365vAWZya+5WaI
D+GUp05Phk2oK42BmKtVbndhFlwXuycl9dRGPR3KH2WD7R/McBIBfYZNKsckf99v
1NVPZ/vD61TP2TvXORRAGFXC8BlBz+bFH/DwFhZ/okEMQoJ5L+Jm0j5toA72LhjT
S0N/KExLNfyRj1z2bzmaFO7qfdnrhK7ipWQSN5jBezF7KJy91BVgVtLhgVpDQXQx
vhmKhpV5v8eTBAB+vK0egfSyGie/ZoTOUOJTABEBAAG0I1Rlc3QgVGVzdCAodGVz
dGluZykgPHRlc3RAdGVzdC5jb20+iQE5BBMBCAAjBQJbw09fAhsDBwsJCAcDAgEG
FQgCCQoLBBYCAwECHgECF4AACgkQZ1P44W01/CQ50Af/exwHBZmOePjiBseVE1/9
vIBYcVTHsGRsO+IMAI4+467ZBzOVG6jAw3jr9cOKh8rIvB/Cx7x6PkorEjHo+cyJ
DzJAPKSqEVAabtGt5BT0SzbPmlLp5qtUvCTEqBC9KK/IVQboOWdCag3aM0UK+YwC
WbOzuZAMeH3pdbazgZATliAQ4br9rOvecdA9/E9HiA4Qp9bYyeCge+PmA/NNaIMb
+TycRKRrutv6I8jZHO8AJMJ7ETyO41FIAJmAPDeHs3kcbbEQUb59w0vUHfvKUmZH
jnw5VoDtfokfJ9dqxGChsFbzPuelrLWqLDqiOTysUImU3O4ZtrvvS68T7Gy6LQIs
KLkBDQRbw09fAQgAolrf1STn6UrgpPZ1kwcrfrMOMJcL4NM4KvkpGf+7Nc0mje4q
+bOyWYFA0M3EsiqiG74BGcWDExNrPwx2fbrAQb7zslUL28RAs+TcBd5q7XcRdrgh
5hvi4VHwAryD4zeMdXNJZUDZSWQEZjgbC49DKh2QlBL1Jjk6WFgQBMzh4S2duNlq
ED7HHYDV9Kfjm9/Uiu0kuD/K9lkWC7MR21UAx/BE4guxP9n42kSCQdRu89ZP7Hd9
GG1+6v0s6P+KaoHAGunQRrlWXFykolAY+aOu8jSEViEv5BNKFbffHJ4me1Fnkzji
wLK2sxnzhTySoel9B3p9bXJgXuMrDVEtXPwtOwARAQABiQEfBBgBCAAJBQJbw09f
AhsMAAoJEGdT+OFtNfwk3rYH/Agn8NP6xIGoeGDzXunLHIdaHwy3IsFrL/JpAKjT
XQK0JYyPONM+NAsUqnSWh/7bRknxuPSbh4gU/ZFV86m7RJI65VHRUT7Wa4O/f8Yq
bpHJRubYVTcV2TcA+cjmZMZCCbt5KkNFHZQ7ZQG4ht3bKRwDSrB/3r8xAyQerOY3
aLAgrsTit5EDPCT1Myxu/6Kmp/AcU7hxh5ag0dvKiphNdhyTXXWXSg6asjkboMo3
5Htdd01lgrnxP7SW/ZehRW9FnOzOX4QTIeAxduD1jxTUdB5kGeAG6RoczWirY1ZT
cMQl0COu/ON36+5saQ5cuSE3vVT34jH9CInJr8/DZy97zhE=
=0Oky
-----END PGP PUBLIC KEY BLOCK-----

$ cat test@test.com
-----BEGIN PGP PRIVATE KEY BLOCK-----

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
=bA77
-----END PGP PRIVATE KEY BLOCK-----


2. Copy keys to `config/keys` folder

$ cp ~/test@test.com.public <monorepo>/config/keys
$ cp ~/test@test.com <monorepo>/config/keys

3. Create a master key (for securing signing key and deploy passwords)

> (require '[hara.security :as security])
    (security/generate-key "AES" {:length 128})
    => #key {:type "AES", :mode :secret, :format "RAW", :encoded "gS4wWJ6plQDKNhfO5zyPzQ=="}

4. Create `~/.hara/global.edn` with master key from (3)

{:hara {:key "gS4wWJ6plQDKNhfO5zyPzQ=="
        :person  "Test User"
        :user    "testuser"
        :email   "test@test.com"}}
    
5. Redefine `hara.config.base.secure/+master-key+` (*hack* will just work if repl is restarted)

> (in-ns 'hara.config.base.secure)
> (def +master-key+ (get-in (global/global) [:hara :key])) 
> (def ^:dynamic *key*
    (if +master-key+
      (try
       (security/->key (assoc +master-defaults+ :encoded +master-key+))
       (catch Throwable t))))

6. Add actual authentication for clojars in `config/repositories.edn`:

{"clojars"  
 {:id       "clojars"
  :url      "https://clojars.org/repo"
  :authentication {:username "<USER>"
                   :password "<PASS>"}}}

7. Secure repositories and private key with master key, creating secured versions

(spit "config/keys/test@test.com.secured"
      (hara.config.base.secure/encrypt-text (slurp "config/keys/test@test.com")))
  
(spit "config/repositories.edn.secured"
      (hara.config.base.secure/encrypt-text (slurp "config/repositories.edn")))
      
8. Now, it's possible to delete the sensitive files from the repo:
  - config/keys/test@test.com
  - config/repositories.edn
  
9. Set `config/deploy.edn`

{:dependencies    [:project :dependencies]
 
 :packages        [:include "config/packages.edn" {:type :edn}]
 
 :releases        {:public  [:include "config/deploy/tech.public.edn" {:type :edn}]
                   :dev     [:include "config/deploy/tech.dev.edn" {:type :edn}]}
 
 ;; make sure {:hara {:email <EMAIL>}} is set in `~/.hara/global.edn`
 :signing         {:key [:include [:str ["config/keys/" [:global :hara.email] ".secured"]]  
                                  {:type :gpg :secured true}]}

 :repositories    [:include "config/repositories.edn.secured" {:type :edn :secured true}]}
 
 
10. Deploy should work

> (require 'hara.deploy)
> (hara.deploy/install-secure [:dev])
> (hara.deploy/deploy [:public])