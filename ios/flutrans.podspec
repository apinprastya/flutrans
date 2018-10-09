#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html
#
Pod::Spec.new do |s|
  s.name             = 'flutrans'
  s.version          = '0.0.2'
  s.summary          = 'A new flutter plugin project.'
  s.description      = <<-DESC
Midtrans Payment Gateway for Flutter.
                       DESC
  s.homepage         = 'https://github.com/apinprastya/flutrans'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'Apin' => 'apin.klas@gmail.com' }
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*'
  s.public_header_files = 'Classes/**/*.h'
  s.dependency 'Flutter'
  s.dependency 'MidtransCoreKit'
  s.dependency 'MidtransKit'
  
  s.ios.deployment_target = '8.0'
end

