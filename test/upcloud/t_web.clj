(ns upcloud.t_web
  (:use [upcloud.web])
  (:use [upcloud.upload])
  (:use [midje.sweet]))

(facts "about the file to be saved"
       (let [upload-id "101110"
             file-name "03 All Star.mp3"
             file-size 3209595
             req {:remote-addr "0:0:0:0:0:0:0:1%0",
                  :scheme :http,
                  :request-method :post,
                  :query-string upload-id,
                  :content-type "multipart/form-data; boundary=----WebKitFormBoundary2c0oHK4tobReX3Ah",
                  :uri "/upload",
                  :server-name "localhost",               
                  :headers {"user-agent" "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_7) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.122 Safari/534.30",
                            "origin" "http://localhost:8081", "accept-charset" "ISO-8859-1,utf-8;q=0.7,*;q=0.3",
                            "accept" "text/html, application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8", "host" "localhost:8081",
                            "referer" " http://localhost:8081/", "content-type" "multipart/form-data; boundary=----WebKitFormBoundary2c0oHK4tobReX3Ah",
                            "cache-control" "max-age=0","accept-encoding" "gzip, deflate,sdch",
                            "content-length" 3209787, "accept-language" "en-US, en;q=0.8", "connection" "keep-alive"},
                  :content-length file-size,
                  :server-port 8081,
                  :character-encoding nil,
                  :body :blablabla}]

         (fact "its name is an mp3 based on upload id"
               (upload-id-for req) => upload-id)

         (fact "the name can only be a number"
               (upload-id-for {:query-string "../../usr/bin/login"}) => (throws NumberFormatException))
         
         (fact "its approximate (file + heaers) size is retrieved from request"
               (approximate-file-size req) => file-size)))

(facts "about the upload handler"
       (fact "it saves the file to the expected directory"
             (let [expected-temp-dir (System/getProperty "java.io.tmpdir")
                   expected-filename "this-should-be-the-filename.mp3"
                   req  {:body (.getBytes "from some random string")}]
               (handler-upload req) => {:status 200}
               (provided
                (upload-id-for req) => expected-filename
                (temp-directory) => expected-temp-dir))))

(facts "about the status handler"
       (fact "should report status for existing upload process"
             (let [upload-id "123123123"
                   req {:query-string upload-id}]
               (handler-status req) => {:status 200 :body "{progress:'13'}"}
               (provided
                (progress-for upload-id) => 13)))
       
       (fact "should return 404 fon non existing upload process"
             (let [upload-id "54321"
                   req {:query-string upload-id}]
               (handler-status req) => {:status 404 :body "No upload in progress"}
               (provided
                (progress-for upload-id) => nil))))
