require 'rubygems'
require 'sinatra'
require 'rest_client'
require 'oauth'
require 'oauth/consumer'
require 'uri'
require 'pp'
require 'json'
require 'stringio'
require 'net/http/post/multipart'

module Kernel
  private
  def pp_s(*objs)
      s = StringIO.new
      objs.each {|obj|
        PP.pp(obj, s)
      }
      s.rewind
      s.read
  end
  module_function :pp_s
end

CRLF = "\r\n"

use Rack::Session::Cookie

before do
  session[:kitchen] ||= {}
  @kitchen = Kitchen.new( session[:kitchen] )
end

error do
  exception = request.env['sinatra.error']
  puts "%s: %s" % [exception.class, exception.message]
  puts exception.backtrace
  "Sorry there was a nasty error"
end

get "/" do
  if @kitchen.ready?
    erb :ready
  else
    erb :start
  end
end

get "/reset" do
  session.clear
  redirect '/'
end

get '/bootstrap' do
  Kitchen.reset
  redirect '/'
end

get "/request" do
  @kitchen.request
  session[:kitchen] = @kitchen.session_data
  redirect @kitchen.url
end

get '/callback' do
  params = request.env[ 'QUERY_STRING' ].split('&').inject({}) do |hsh, i| kv = i.split('='); hsh[kv[0]] = kv[1]; hsh end
  @kitchen.upgrade params
  session[:kitchen] = @kitchen.session_data
  redirect '/'
end

get '/jobs/all' do
  response = @kitchen.get("/jobs?allOwners=true")
  if response.code != "200" then
    halt "#{response.code} #{response.msg}"
  end
  data = JSON.parse( response.body )
  "<html><body><pre>" + pp_s( data ) + "</pre></body></html>"
end

get '/jobs/mine' do
  response = @kitchen.get("/jobs")
  if response.code != "200" then
    halt "#{response.code} #{response.msg}"
  end
  data = JSON.parse( response.body )
  "<html><body><pre>" + pp_s( data ) + "</pre></body></html>"
end

get '/jobs/summary/all' do
  response = @kitchen.get("/jobs/summary?allOwners=true")
  if response.code != "200" then
    halt "#{response.code} #{response.msg}"
  end
  data = JSON.parse( response.body )
  "<html><body><pre>" + pp_s( data ) + "</pre></body></html>"
end

get '/jobs/summary/mine' do
  response = @kitchen.get("/jobs/summary")
  if response.code != "200" then
    halt "#{response.code} #{response.msg}"
  end
  data = JSON.parse( response.body )
  "<html><body><pre>" + pp_s( data ) + "</pre></body></html>"
end

get '/jobs/grouped/flatten/all' do
  response = @kitchen.get("/jobs/groupby/status?allOwners=true&status=Waiting,Done,Failed,Completed,Staging&maxJobs=1&flatten=true")
  if response.code != "200" then
    halt "#{response.code} #{response.msg}"
  end
  data = JSON.parse( response.body )
  "<html><body><pre>" + pp_s( data ) + "</pre></body></html>"
end

get '/jobs/grouped/flatten/mine' do
  response = @kitchen.get("/jobs/groupby/status?status=Waiting,Done,Failed,Completed,Staging&maxJobs=1&flatten=true")
  if response.code != "200" then
    halt "#{response.code} #{response.msg}"
  end
  data = JSON.parse( response.body )
  "<html><body><pre>" + pp_s( data ) + "</pre></body></html>"
end

get '/jobs/grouped/all' do
  response = @kitchen.get("/jobs/groupby/status?allOwners=true&status=Waiting,Done,Failed,Completed,Staging&maxJobs=1")
  if response.code != "200" then
    halt "#{response.code} #{response.msg}"
  end
  data = JSON.parse( response.body )
  "<html><body><pre>" + pp_s( data ) + "</pre></body></html>"
end

get '/jobs/grouped/mine' do
  response = @kitchen.get("/jobs/groupby/status?status=Waiting,Done,Failed,Completed,Staging&maxJobs=1")
  if response.code != "200" then
    halt "#{response.code} #{response.msg}"
  end
  data = JSON.parse( response.body )
  "<html><body><pre>" + pp_s( data ) + "</pre></body></html>"
end

get '/jobs/history' do
    response = @kitchen.get("/jobs/history?allOwners=true")
      if response.code != "200" then
            halt "#{response.code} #{response.msg}"
              end
        data = JSON.parse( response.body )
          "<html><body><pre>" + pp_s( data ) + "</pre></body></html>"
end

get '/jobs/history/user' do
    response = @kitchen.get( "/jobs/history" )
      if response.code != "200" then
            halt "#{response.code} #{response.msg}"
              end
        data = JSON.parse( response.body )
          "<html><body><pre>" + pp_s( data ) + "</pre></body></html>"
end

post '/jobs/dosubmit' do
  jobData = {}
  [ "Name", "Executable", "Arguments" ].each do |k|
    if not  params.include? k then
      halt "400", "Missing #{k}"
    end
    jobData[ k ] = params[ k ]
  end
  if params.include? "Sandbox" then
    jobData[ 'InputSandbox' ] == [ params[ 'Sandbox' ] ]
  end
  jobData = { 'job' => jobData.to_json() }
  [ "f1", "f2" ].each do |k|
    if params.include? k then
      jobData[ params[ k ][ :filename ] ] = UploadIO.new( params[k][:tempfile], "application/octet-stream" )
    end
  end
  
  url = URI.parse(@kitchen.APIURL + '/jobs' )
  Net::HTTP.new(url.host, url.port).start do |http|
    req = Net::HTTP::Post::Multipart.new( url.path,  jobData )
       
    @kitchen.access_token.sign! req
    
    res = http.request(req)
    "<html><body><pre>"+ pp_s( res.body ) + "</pre></body></html>"
  end
end


get '/jobs/:jid' do
  response = @kitchen.get("/jobs/#{params[:jid]}")
  if response.code != "200" then
    halt "#{response.code} #{response.msg}"
  end
  data = JSON.parse( response.body )
  "<html><body><pre>" + pp_s( data ) + "</pre></body></html>"
end

get '/jobs/:jid/description' do
  response = @kitchen.get("/jobs/#{params[:jid]}/description")
  if response.code != "200" then
    halt "#{response.code} #{response.msg}"
  end
  data = JSON.parse( response.body )
  "<html><body><pre>" + pp_s( data ) + "</pre></body></html>"
end


get '/sb/upload' do
  erb :uploadsb
end

post '/sb/doupload' do
  sendParams = {}
  params.each_pair do |k,v|
    sendParams[k] = params[k][:tempfile]
  end
  

  url = URI.parse(@kitchen.APIURL + '/sandbox/input' )
  Net::HTTP.new(url.host, url.port).start do |http|
    req = Net::HTTP::Post.new(url.request_uri)
    
    add_multipart_data(req, sendParams)
    @kitchen.consumer.sign!( req, @kitchen.access_token )
    res = http.request(req)
    "<html><body><pre>"+ pp_s( res.body ) + "</pre></body></html>"
  end

end

def add_multipart_data(req,params)
  boundary = Time.now.to_i.to_s(16)
  req["Content-Type"] = "multipart/form-data; boundary=#{boundary}"
  body = ""
  params.each do |key,value|
    esc_key = CGI.escape(key.to_s)
    body << "--#{boundary}#{CRLF}"
    if value.respond_to?(:read)
      body << "Content-Disposition: form-data; name=\"#{esc_key}\"; filename=\"#{File.basename(value.path)}\"#{CRLF}"
      #body << "Content-Type: text/xml#{CRLF*2}"
      body << "Content-Type: application/zip#{CRLF*2}"
      body << value.read
    else
      body << "Content-Disposition: form-data; name=\"#{esc_key}\"#{CRLF*2}#{value}"
      body << "Content-Type: text/plain#{CRLF*2}"
    end
    body << CRLF
  end
  body << "--#{boundary}--#{CRLF*2}"
  req.body = body
  req["Content-Length"] = req.body.size
end


get '/sb/list/:type/:id' do
  response = @kitchen.get("/sandbox/list/#{params[:type]}/#{params[:id]}")
  if response.code != "200" then
    halt "#{response.code} #{response.msg}"
  end
  data = JSON.parse( response.body )
  "<html><body><pre>" + pp_s( data ) + "</pre></body></html>"
end

get '/sb' do
  if not params.include? 'sburl'
    halt "400" "Missing sburl"
  end
  sburl = CGI.escape( params[ 'sburl' ] )
  response = @kitchen.get("/sandbox?sburl=#{sburl}")
    if response.code != "200" then
      halt "#{response.code} #{response.msg}"
    end
    data = JSON.parse( response.body )
    "<html><body><pre>" + pp_s( data ) + "</pre></body></html>"
end

#################################
#
# Kitchen class
#
################################

class Kitchen
  attr_accessor :APIURL
  

  def self.reset
    RestClient.delete("#{APIURL}/db")
  end

  def initialize(options = {})
    @APIURL = "http://lhcb01.ecm.ub.es:9354"
    request_token, request_token_secret = options[:request_token], options[:request_token_secret]
    if request_token && request_token_secret
      @request_token = OAuth::RequestToken.new(consumer, request_token, request_token_secret)
    end

    access_token, access_token_secret = options[:access_token], options[:access_token_secret]
    if access_token && access_token_secret
      @access_token = OAuth::AccessToken.new(consumer, access_token, access_token_secret)
    end
  end

  def ready?
    @access_token
  end

  def request
    @request_token = consumer.get_request_token( { :oauth_callback => "http://lhcb08:4567/callback" } )
  end

  def session_data
    data = {}
    if @request_token
      data[:request_token] = @request_token.token
      data[:request_token_secret] = @request_token.secret
    end
    if @access_token
      data[:access_token] = @access_token.token
      data[:access_token_secret] = @access_token.secret
    end
    data
  end

  def url
    @request_token.authorize_url
  end

  def upgrade( params )
    @access_token = @request_token.get_access_token ( { :oauth_verifier => params[ 'oauth_verifier' ] } )
  end

  def get(*args)
    @access_token.get(*args)
  end
  
  def post(*args)
      @access_token.post(*args)
  end

  def consumer
    @consumer ||= OAuth::Consumer.new( "be99ba61cbe5fcba8d631d0135ea6294",
                                       #"00dfda4e8fe90b0a42697b0070635303",
                                       "7d86c156217881b4dae0d046ade697c4",
                                       :site => @APIURL )
  end
  
  def access_token
    @access_token || false
  end
  
end


__END__

@@ start
<a href="/request">Get access</a>

@@ ready
<h2>You are ready for access!</h2>
<a href="/jobs/all">Get all jobs</a><br/>
<a href="/jobs/mine">Get only my jobs</a><br/>
<a href="/jobs/submit">Submit a test job</a><br/>
<hr>
<a href="/sb/upload">Upload SB</a><br/>
<hr>
<a href="/reset">Reset token</a><br/>

@@ uploadsb
<form method='post' action='doupload' enctype='multipart/form-data'>
<input type='file' name='f1'/><br/>
<input type='file' name='f2'/><br/>
<input type='submit'/><br/>
</form>

@@ submitjob
<form method='post' action='dosubmit' enctype='multipart/form-data'>
<table>
<tr><td>Job Name</td><td><input type='text' name='Name' value='test job'/></td></tr>
<tr><td>Executable</td><td><input type='text' name='Executable' value='/bin/echo'/><br/></td></tr>
<tr><td>Arguments</td><td><input type='text' name='Arguments' value='Hello World'/><br/></td></tr>
<tr><td>SandboxURL</td><td><input type='text' name='SB' value=''/><br/></td></tr>
<tr><td>Sandbox file 1</td><td><input type='file' name='f1'/><br/></td></tr>
<tr><td>Sadnbox file 2</td><td><input type='file' name='f2'/><br/></td></tr>
</table>
<input type='submit'/><br/>
</form>
