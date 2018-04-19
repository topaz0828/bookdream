const UglifyJsPlugin = require('uglifyjs-webpack-plugin')
 
module.exports = {
    entry: './src/index.js',

    output: {
        path: __dirname + '/../web-service/webapp/js',
        filename: 'bundle.js'
    },

    devServer: {
        inline: true,
        port: 7777,
        contentBase: __dirname + '/public/'
    },

    module: {
            loaders: [
                {
                    test: /\.js$/,
                    loader: 'babel-loader',
                    exclude: /node_modules/,
                    query: {
                        cacheDirectory: true,
                        presets: ['es2015', 'react']
                    }
                }
            ]
        },

   plugins: [
       new UglifyJsPlugin()
     ]
};
