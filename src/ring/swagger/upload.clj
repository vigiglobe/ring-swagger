(ns ring.swagger.upload
  (:require [potemkin :refer [import-vars]]
            [ring.middleware.multipart-params]
            [ring.swagger.json-schema :as js]
            [schema.core :as s])
  (:import [java.io File]))

(import-vars
  [ring.middleware.multipart-params

   wrap-multipart-params])

; Works exactly like map schema but wrapped in record for JsonSchema dispatch
(defrecord Upload [m]

  s/Schema
  (walker [_]
    (let [sub-walker (s/subschema-walker m)]
      (clojure.core/fn [x]
       (if (schema.utils/error? x)
         x
         (sub-walker x)))))
  (explain [_]
    (cons 'file m))

  js/JsonSchema
  (convert [_ _]
    {:type "file"}))

(def TempFileUpload
  "Schema for file param created by ring.middleware.multipart-params.temp-file store."
  (->Upload {:filename s/Str
             :content-type s/Str
             :size s/Int
             (s/optional-key :tempfile) File}))

(def ByteArrayUpload
  "Schema for file param created by ring.middleware.multipart-params.byte-array store."
  (->Upload {:filename s/Str
             :content-type s/Str
             :bytes s/Any}))
